package com.example.spbtex.ui.history;

public class HistoryData {

    private Long id;
    private Long memberId;
    private Long facilityId;
    private String memberName;
    private String facilityName;
    private String rday;
    private String rstart;
    private String rend;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
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

    public HistoryData() {
    }

    public HistoryData(Long id, Long memberId, Long facilityId, String memberName, String facilityName, String rday, String rstart, String rend) {
        this.id = id;
        this.memberId = memberId;
        this.facilityId = facilityId;
        this.memberName = memberName;
        this.facilityName = facilityName;
        this.rday = rday;
        this.rstart = rstart;
        this.rend = rend;
    }
}
