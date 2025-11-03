package com.example.digitallogistics.model.dto;

public class WarehouseUpdateDto {
    private String code;
    private String name;
    private Boolean active;

    public WarehouseUpdateDto() {
    }

    public WarehouseUpdateDto(String code, String name, Boolean active) {
        this.code = code;
        this.name = name;
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}