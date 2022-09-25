package com.fc.sns.model.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;


@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE \"like\" SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
@Table(name = "\"like\"", indexes = {
        @Index(name = "user_id_idx", columnList = "user_id")
})
@Entity
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity postEntity;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;


    private LikeEntity(UserEntity user, PostEntity postEntity) {
        this.user = user;
        this.postEntity = postEntity;
    }

    public static LikeEntity of(UserEntity user, PostEntity postEntity) {
        return new LikeEntity(user, postEntity);
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
