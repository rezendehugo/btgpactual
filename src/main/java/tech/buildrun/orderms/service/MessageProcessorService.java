package tech.buildrun.orderms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.buildrun.orderms.config.RabbitMqConfig;
import tech.buildrun.orderms.listener.dto.OrderCreatedEvent;

@Service
public class MessageProcessorService {

    private final RabbitTemplate rabbitTemplate;

    private final Logger logger = LoggerFactory.getLogger(MessageProcessorService.class);

    @Autowired
    public MessageProcessorService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMqConfig.CREATE_ORDER_QUEUE)
    public void processMessage(OrderCreatedEvent message) {
        logger.info("Received message from source queue: {}", message);

        var entity = new OrderCreatedEvent(message.codigoPedido(), message.codigoCliente(), message.itens());;
        
        rabbitTemplate.convertAndSend(RabbitMqConfig.ORDER_CREATED_QUEUE, message);
        logger.info("Message sent to target queue: {}", entity);
    }
}
