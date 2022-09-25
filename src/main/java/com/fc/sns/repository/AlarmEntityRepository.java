package com.fc.sns.repository;

import com.fc.sns.model.entity.AlarmEntity;
import com.fc.sns.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmEntityRepository extends JpaRepository<AlarmEntity, Long> {

    Page<AlarmEntity> findAllByUser(UserEntity user, Pageable pageable);

}
