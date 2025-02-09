package com.example.spbtex;

public class SessionData {
    private String sessionId;
    private String memberId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public SessionData(String sessionId, String memberId) {
        this.sessionId = sessionId;
        this.memberId = memberId;
    }

    public SessionData() {
    }
}
