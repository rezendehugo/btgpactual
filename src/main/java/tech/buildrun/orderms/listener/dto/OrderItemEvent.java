package tech.buildrun.orderms.listener.dto;

import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import java.math.BigDecimal;

public record OrderItemEvent(String produto,
                            Integer quantidade,
                            BigDecimal preco) {

    
}
