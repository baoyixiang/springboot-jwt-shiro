package com.bao.shirojwt.domain.user;

import java.io.Serializable;
import java.util.Date;

public class LoginInfo implements Serializable {
    private String lastLoginSalt;
    private Date lastLoginTime;

    public String getLastLoginSalt() {
        return lastLoginSalt;
    }

    public void setLastLoginSalt(String lastLoginSalt) {
        this.lastLoginSalt = lastLoginSalt;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
