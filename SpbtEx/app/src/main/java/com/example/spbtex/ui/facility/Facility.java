package com.example.spbtex.ui.facility;

public class Facility {

    private Long id;
    private String name;
    private Integer amount;
    private String memo;

    public Facility(Long id, String name, Integer amount, String memo) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.memo = memo;
    }

    public Facility() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

}
