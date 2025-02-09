package com.example.spbtex.ui.history;

import androidx.annotation.Nullable;

import com.example.spbtex.ui.facility.Facility;
import com.example.spbtex.ui.member.Member;

public class ReservationForm {

    private Long id;
    private Member member;
    private Facility facility;
    private String rday;
    private String rstart;
    private String rend;
    @Nullable
    private String deleteDateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
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

    @Nullable
    public String getDeleteDateTime() {
        return deleteDateTime;
    }

    public void setDeleteDateTime(@Nullable String deleteDateTime) {
        this.deleteDateTime = deleteDateTime;
    }

    public ReservationForm() {
    }

    public ReservationForm(Long id, Member member, Facility facility, String rday, String rstart, String rend, @Nullable String deleteDateTime) {
        this.id = id;
        this.member = member;
        this.facility = facility;
        this.rday = rday;
        this.rstart = rstart;
        this.rend = rend;
        this.deleteDateTime = deleteDateTime;
    }
}
