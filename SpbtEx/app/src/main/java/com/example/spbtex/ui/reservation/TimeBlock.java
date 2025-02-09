package com.example.spbtex.ui.reservation;

public class TimeBlock {
    private String rday;
    private String rstart;
    private String rend;
    private boolean reserved;

    public TimeBlock() {
    }

    public TimeBlock(String rday, String rstart, String rend, boolean reserved) {
        this.rday = rday;
        this.rstart = rstart;
        this.rend = rend;
        this.reserved = reserved;
    }

    public String getRday() {
        return rday;
    }

    public void setRday(String rday) {
        this.rday = rday;
    }

    public String getRstart() {
        return rstart;
    }

    public void setRstart(String rstart) {
        this.rstart = rstart;
    }

    public String getRend() {
        return rend;
    }

    public void setRend(String rend) {
        this.rend = rend;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }
}
