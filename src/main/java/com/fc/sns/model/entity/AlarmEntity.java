package com.fc.sns.model.entity;

import com.fc.sns.model.AlarmArgs;
import com.fc.sns.model.AlarmType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;


@Setter
@Getter
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE \"alarm\" SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
@Table(name = "\"alarm\"")
@Entity
public class AlarmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알람을 받은 사람
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private AlarmArgs args;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;


    private AlarmEntity(UserEntity user, AlarmType alarmType, AlarmArgs args) {
        this.user = user;
        this.alarmType = alarmType;
        this.args = args;
    }

    public static AlarmEntity of(UserEntity user, AlarmType alarmType, AlarmArgs args) {
        return new AlarmEntity(user, alarmType, args);
    }

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

}
