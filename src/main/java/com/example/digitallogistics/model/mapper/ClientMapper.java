package com.example.digitallogistics.model.mapper;

import com.example.digitallogistics.model.dto.ClientCreateDto;
import com.example.digitallogistics.model.dto.ClientDto;
import com.example.digitallogistics.model.entity.Client;
import com.example.digitallogistics.model.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientDto toDto(Client client) {
        if (client == null) {
            return null;
        }
        
        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setContact(client.getContact());
        dto.setActive(client.isActive());
        dto.setRole(Role.CLIENT);
        return dto;
    }

    public Client toEntity(ClientCreateDto clientCreateDto) {
        if (clientCreateDto == null) {
            return null;
        }
        
        Client client = new Client();
        client.setName(clientCreateDto.getName());
        client.setContact(clientCreateDto.getContact());
        client.setActive(clientCreateDto.getActive() != null ? clientCreateDto.getActive() : true);
        client.setRole(Role.CLIENT);
        return client;
    }
}