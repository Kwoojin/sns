package com.fc.sns.model;

import lombok.Getter;

@Getter
public enum AlarmType {
    NEW_COMMENT_ON_POST("new comment!"),
    NEW_LIKE_ON_POST("new like!"),
    ;

    private final String alarmText;

    AlarmType(String alarmText) {
        this.alarmText = alarmText;
    }
}
