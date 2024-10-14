package tech.buildrun.orderms.service;

import java.math.BigDecimal;
import java.util.List;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import tech.buildrun.orderms.controller.dto.OrderResponse;
import tech.buildrun.orderms.entity.OrderEntity;
import tech.buildrun.orderms.entity.OrderItem;
import tech.buildrun.orderms.listener.dto.OrderCreatedEvent;
import tech.buildrun.orderms.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final MongoTemplate mongoTemplate;

    public OrderService(OrderRepository orderRepository, MongoTemplate mongoTemplate){
        this.orderRepository = orderRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public void save(OrderCreatedEvent event){

        var entity = new OrderEntity();
        entity.setOrderId(event.codigoPedido());
        entity.setCustomerId(event.codigoCliente());
        entity.setItems(getOrderItens(event));
        entity.setTotal(getTotal(event));

        orderRepository.save(entity);
        
    }

    public Page<OrderResponse> findByCustomer(Long customerId, PageRequest pageRequest){
        var orders =  orderRepository.findAllByCustomerId(customerId, pageRequest);
        System.out.println("Orders: " + orders.getContent());
        
        return orders.map(OrderResponse::fromEntity);
    }

    public BigDecimal findTotalOnOrdersByCustomerId(Long customerId) {
        var aggregations = newAggregation(
                match(Criteria.where("customerId").is(customerId)),
                group().sum("total").as("total")
        );

        var response = mongoTemplate.aggregate(aggregations, "tb_orders", Document.class);

        return new BigDecimal(response.getUniqueMappedResult().get("total").toString());
    }

    private BigDecimal getTotal(OrderCreatedEvent event) {
        return event.itens()
                .stream()
                .map(i -> i.preco().multiply(BigDecimal.valueOf(i.quantidade())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private static List<OrderItem> getOrderItens(OrderCreatedEvent event) {
        return event.itens().stream()
                .map(i -> new OrderItem(i.produto(), i.quantidade(), i.preco()))
                .toList();
    }

}
