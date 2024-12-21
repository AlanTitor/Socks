package com.AlanTitor.socks.DTO;

public class Socks {
    private Long id;
    private String color;
    private Double percentCotton;
    private Integer amount;

    public Socks(Long id, String color, Double percentCotton, Integer amount){
        this.id = id;
        this.color = color;
        this.percentCotton = percentCotton;
        this.amount = amount;
    }

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
