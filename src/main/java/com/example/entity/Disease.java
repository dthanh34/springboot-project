package com.example.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "Disease")
public class Disease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Disease_id" )
    private Integer Disease_id;
    private String Disease_name;
    private String disease_description;

    public Integer getDisease_id() { return Disease_id; }
    public void setDisease_id(Integer Disease_id) { this.Disease_id = Disease_id; }
    
    public String getDisease_name (){ return Disease_name;}
    public void setDisease_name (String Disease_name){ this.Disease_name = Disease_name ;}

    public String getDisease_description() { return disease_description;}
    public void setDisease_description(String disease_description) { this.disease_description = disease_description;}
}
