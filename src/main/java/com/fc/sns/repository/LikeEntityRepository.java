package com.fc.sns.repository;

import com.fc.sns.model.entity.LikeEntity;
import com.fc.sns.model.entity.PostEntity;
import com.fc.sns.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikeEntityRepository extends JpaRepository<LikeEntity, Long> {

    Optional<LikeEntity> findByUserAndPostEntity(UserEntity user, PostEntity postEntity);

    @Query(value = "SELECT COUNT(*) FROM LikeEntity entity WHERE entity.postEntity =:postEntity")
    Integer countByPostEntity(PostEntity postEntity);

    List<LikeEntity> findAllByPostEntity(PostEntity postEntity);
}
