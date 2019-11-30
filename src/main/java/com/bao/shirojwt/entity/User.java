package com.bao.shirojwt.entity;

import java.util.List;

public class User {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String lastLoginSalt;
    private String roles;
    private String encryptSalt;
    private String lastLoginToken;

    public String getLastLoginToken() {
        return lastLoginToken;
    }

    public void setLastLoginToken(String lastLoginToken) {
        this.lastLoginToken = lastLoginToken;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getLastLoginSalt() {
        return lastLoginSalt;
    }

    public void setLastLoginSalt(String lastLoginSalt) {
        this.lastLoginSalt = lastLoginSalt;
    }

    public String getEncryptSalt() {
        return encryptSalt;
    }

    public void setEncryptSalt(String encryptSalt) {
        this.encryptSalt = encryptSalt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
