package tech.buildrun.orderms.controller;

import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tech.buildrun.orderms.controller.dto.ApiResponse;
import tech.buildrun.orderms.controller.dto.OrderResponse;
import tech.buildrun.orderms.controller.dto.PaginationResponse;
import tech.buildrun.orderms.listener.dto.OrderCreatedEvent;
import tech.buildrun.orderms.producer.CreateOrderProducer;
import tech.buildrun.orderms.service.OrderService;

@RestController
public class OrderController {

    private final OrderService orderService;

    private final CreateOrderProducer createOrderProducer;

    public OrderController(OrderService orderService, CreateOrderProducer createOrderProducer){
        this.orderService = orderService;
        this.createOrderProducer = createOrderProducer;
    }

    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> listOrders(@PathVariable("customerId") Long customerId,
        @RequestParam(name = "Page", defaultValue = "0") Integer page,
        @RequestParam(name = "PageSize", defaultValue = "10") Integer pageSize){
        
        var pageResponse = orderService.findByCustomer(customerId, PageRequest.of(page, pageSize));
        var totalOnOrders = orderService.findTotalOnOrdersByCustomerId(customerId);

        System.out.println("PageResponse: " + pageResponse.getContent());

        return ResponseEntity.ok(new ApiResponse<>(Map.of("totalOnOrders", totalOnOrders) 
                                                    ,pageResponse.getContent(), 
                                                    PaginationResponse.fromPage(pageResponse)));
    }

    @PostMapping("/customers/create")
    public ResponseEntity<String> createOrderEvent(@RequestBody OrderCreatedEvent orderItem){
            String orderId = createOrderProducer.sendOrder(orderItem);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(orderId);
    }
}
