package tech.buildrun.orderms.listener.dto;
import java.util.List;
import tech.buildrun.orderms.listener.dto.OrderItemEvent;

public record OrderCreatedEvent(Long codigoPedido,
                                Long codigoCliente,
                                List<OrderItemEvent> itens) {

} 
