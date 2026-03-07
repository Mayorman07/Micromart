package com.micromart.Payment.configuration;

import com.micromart.Payment.model.dto.OrderItemDto;
import com.micromart.Payment.model.request.OrderItemRequest;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(OrderItemRequest.class, OrderItemDto.class);

        return modelMapper;
    }
}
