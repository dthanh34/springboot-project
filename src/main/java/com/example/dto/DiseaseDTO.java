package com.example.dto;

public class DiseaseDTO {
    private Integer disease_id;
    private String disease_name;
    private String disease_description;

    public Integer getDisease_id() {
        return disease_id;
    }

    public void setDisease_id(Integer disease_id) {
        this.disease_id = disease_id;
    }

    public String getDisease_name() {
        return disease_name;
    }

    public void setDisease_name(String disease_name) {
        this.disease_name = disease_name;
    }

    public String getDisease_description() {
        return disease_description;
    }

    public void setDisease_description(String disease_description) {
        this.disease_description = disease_description;
    }
}