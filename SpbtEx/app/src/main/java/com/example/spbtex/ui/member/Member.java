package com.example.spbtex.ui.member;

import androidx.annotation.Nullable;

import com.example.spbtex.ui.history.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public class Member {

    private Long id;
    private String email;
    private String name;
    private String password;
    @Nullable
    private LocalDateTime deleteDateTime;
    @Nullable
    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Nullable
    public LocalDateTime getDeleteDateTime() {
        return deleteDateTime;
    }

    public void setDeleteDateTime(@Nullable LocalDateTime deleteDateTime) {
        this.deleteDateTime = deleteDateTime;
    }

    @Nullable
    public Integer getVersion() {
        return version;
    }

    public void setVersion(@Nullable Integer version) {
        this.version = version;
    }

    public Member() {
    }

    //メール登録用
    public Member(String email) {
        this.email = email;
    }

    //ログイン用
    public Member(String email, String password) {
        this.email = email;
        this.password = password;
    }

    //会員情報登録用
    public Member(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    //会員情報更新用
    //id,email,nameの３つで被るが実際は登録者や更新者などプロパティを増やして区別する

    //SQLiteで1人分の全データ入りのMemberを作成する用
    public Member(Long id, String email, String name, String password, @Nullable LocalDateTime deleteDateTime, @Nullable Integer version) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.deleteDateTime = deleteDateTime;
        this.version = version;
    }
}
