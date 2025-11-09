package com.example.digitallogistics.model.mapper;

import com.example.digitallogistics.model.dto.ManagerCreateDto;
import com.example.digitallogistics.model.dto.ManagerDto;
import com.example.digitallogistics.model.dto.ManagerUpdateDto;
import com.example.digitallogistics.model.entity.Manager;
import com.example.digitallogistics.model.entity.Warehouse;
import com.example.digitallogistics.model.enums.Role;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ManagerMapper {

    public ManagerDto toDto(Manager manager) {
        if (manager == null) {
            return null;
        }
        
        ManagerDto dto = new ManagerDto();
        dto.setId(manager.getId());
        dto.setEmail(manager.getEmail());
        
        if (manager.getWarehouses() != null) {
            dto.setWarehouseIds(
                manager.getWarehouses().stream()
                    .map(Warehouse::getId)
                    .collect(Collectors.toList())
            );
        }
        
        dto.setActive(manager.isActive());
        return dto;
    }

    public Manager toEntity(ManagerCreateDto managerCreateDto) {
        if (managerCreateDto == null) {
            return null;
        }
        
        Manager manager = new Manager();
        manager.setEmail(managerCreateDto.getEmail());
        manager.setPassword(managerCreateDto.getPassword());
        manager.setActive(managerCreateDto.getActive() != null ? managerCreateDto.getActive() : true);
        manager.setRole(Role.WAREHOUSE_MANAGER);
    
        return manager;
    }

    public void updateEntityFromDto(ManagerUpdateDto managerUpdateDto, Manager manager) {
        if (managerUpdateDto == null || manager == null) {
            return;
        }
        
        if (managerUpdateDto.getEmail() != null) {
            manager.setEmail(managerUpdateDto.getEmail());
        }
        if (managerUpdateDto.getPassword() != null) {
            manager.setPassword(managerUpdateDto.getPassword());
        }
        if (managerUpdateDto.getActive() != null) {
            manager.setActive(managerUpdateDto.getActive());
        }
    }
}