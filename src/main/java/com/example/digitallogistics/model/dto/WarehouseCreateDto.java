package com.example.digitallogistics.model.dto;

public class WarehouseCreateDto {
    private String code;
    private String name;

    private Boolean active;

    public WarehouseCreateDto() {
    }


    public WarehouseCreateDto(String code , String name, Boolean active) {
        this.code = code;
        this.active = active;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
