package com.AlanTitor.socks.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "socks")
public class SocksEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "color", nullable = false)
    private String color;
    @Column(name = "percent_cotton", nullable = false)
    private Double percentCotton;
    @Column(name = "amount", nullable = false)
    private Integer amount;

    public SocksEntity(Long id, String color, Double percentCotton, Integer amount){
        this.id = id;
        this.color = color;
        this.percentCotton = percentCotton;
        this.amount = amount;
    }
    public SocksEntity(){}

    public Long getId(){
        return this.id;
    }
    public String getColor(){
        return this.color;
    }
    public Double getPercentCotton(){
        return this.percentCotton;
    }
    public Integer getAmount(){
        return this.amount;
    }

    public void setId(Long id){
        this.id = id;
    }
    public void setColor(String color){
        this.color = color;
    }
    public void setPercentCotton(Double percentCotton){
        this.percentCotton = percentCotton;
    }
    public void setAmount(Integer amount){
        this.amount = amount;
    }
}
