/*
 * Copyright (c) 2021 Several Circles.
 */

package com.severalcircles.flames.data.user;

public enum UserSetting {
    LOCALE();
    @SuppressWarnings("FieldMayBeFinal")
    private String code;
    UserSetting() {
        this.code = "1";
    }
    public String getCode() {
        return code;
    }
}
