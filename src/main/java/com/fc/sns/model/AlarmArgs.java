package com.fc.sns.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlarmArgs {

    // 알림을 발생시킨 사람
    private Long fromUserId;
    private Long targetId;
}
