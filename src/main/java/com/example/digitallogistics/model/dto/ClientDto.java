package com.example.digitallogistics.model.dto;

import java.util.UUID;

import com.example.digitallogistics.model.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDto {
    private UUID id;
    private String name;
    private String contact;
    private Boolean active;
    private Role role;
}
