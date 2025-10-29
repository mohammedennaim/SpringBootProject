package com.example.digitallogistics.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.example.digitallogistics.model.dto.UserCreateDto;
import com.example.digitallogistics.model.dto.UserDto;
import com.example.digitallogistics.model.dto.UserUpdateDto;
import com.example.digitallogistics.model.entity.User;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDto(User user);

    // creation: ignore id mapping (will be assigned by service)
    @Mapping(target = "id", ignore = true)
    User toEntity(UserCreateDto dto);

    // when updating, ignore id as it's part of the path
    @Mapping(target = "id", ignore = true)
    void updateFromDto(UserUpdateDto dto, @MappingTarget User entity);
}
