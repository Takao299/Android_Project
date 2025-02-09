package com.example.spbtex.ui.member;

import com.example.spbtex.SessionData;

public class UpdatePassData {
    private String email;
    private String password0;
    private String password1;
    private SessionData sessionData;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword0() {
        return password0;
    }

    public void setPassword0(String password0) {
        this.password0 = password0;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public SessionData getSessionData() {
        return sessionData;
    }

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }

    //退会用
    public UpdatePassData(String email, SessionData sessionData) {
        this.email = email;
        this.sessionData = sessionData;
    }

    //パスワード変更用
    public UpdatePassData(String email, String password0, String password1, SessionData sessionData) {
        this.email = email;
        this.password0 = password0;
        this.password1 = password1;
        this.sessionData = sessionData;
    }
}
