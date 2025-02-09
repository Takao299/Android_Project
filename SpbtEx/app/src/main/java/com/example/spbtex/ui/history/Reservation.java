package com.example.spbtex.ui.history;

import androidx.annotation.Nullable;

public class Reservation {

    private Long id;
    private Long memberId;
    private Long facilityId;
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

    public Long getMemberId() {return memberId;}

    public void setMemberId(Long memberId) {this.memberId = memberId;}

    public Long getFacilityId() {return facilityId;}

    public void setFacilityId(Long facilityId) {this.facilityId = facilityId;}

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


    public Reservation() {
    }

    //予約キャンセル用
    public Reservation(String member_id, String r_id) {
        this.memberId = Long.valueOf(member_id);
        this.id = Long.valueOf(r_id);
    }

    public Reservation(String member_id, String facility_id, String rday, String rstart, String rend) {
        this.memberId = Long.valueOf(member_id);
        this.facilityId = Long.valueOf(facility_id);
        this.rday = rday;
        this.rstart = rstart;
        this.rend = rend;
    }

}
