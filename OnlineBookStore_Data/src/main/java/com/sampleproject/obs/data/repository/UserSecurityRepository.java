package com.sampleproject.obs.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.sampleproject.obs.data.model.User;
import com.sampleproject.obs.data.model.UserSecurity;
@Repository
public interface UserSecurityRepository extends JpaRepository<UserSecurity, Long>,PagingAndSortingRepository<UserSecurity, Long>{

	Optional<UserSecurity> findByUser(User user);

	Optional<UserSecurity> findByUserId(String userId);
}
