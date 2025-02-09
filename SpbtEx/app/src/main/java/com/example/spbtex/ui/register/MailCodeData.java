package com.example.spbtex.ui.register;

import com.example.spbtex.SessionData;

public class MailCodeData {
    private String email;
    private String code;
    private String pageSessionId;
    private SessionData loginSession;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPageSessionId() {
        return pageSessionId;
    }

    public void setPageSessionId(String pageSessionId) {
        this.pageSessionId = pageSessionId;
    }

    public SessionData getLoginSession() {
        return loginSession;
    }

    public void setLoginSession(SessionData loginSession) {
        this.loginSession = loginSession;
    }

    //新規会員登録用
    public MailCodeData(String email, String code, String pageSessionId) {
        this.email = email;
        this.code = code;
        this.pageSessionId = pageSessionId;
    }

    //会員情報更新用
    public MailCodeData(String email, String code, String pageSessionId, SessionData loginSession) {
        this.email = email;
        this.code = code;
        this.pageSessionId = pageSessionId;
        this.loginSession = loginSession;
    }
}
