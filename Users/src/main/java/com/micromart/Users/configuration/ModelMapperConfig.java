package com.micromart.Users.configuration;

import com.micromart.Users.entities.User;
import com.micromart.Users.models.data.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);

        // This prevents the request from setting the ID field
        TypeMap<UserDto, User> dtoToEntityMap = modelMapper.createTypeMap(UserDto.class, User.class);
        dtoToEntityMap.addMappings(mapper -> mapper.skip(User::setId));

        // This skips the 'roles' field so you can map it manually
        TypeMap<User, UserDto> entityToDtoMap = modelMapper.createTypeMap(User.class, UserDto.class);
        entityToDtoMap.addMappings(mapper -> mapper.skip(UserDto::setRoles));

        return modelMapper;
    }
}
