package com.sampleproject.obs.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sampleproject.obs.data.model.User;
import com.sampleproject.obs.data.model.UserInfo;


public interface UserInfoRepository extends JpaRepository<UserInfo, String>, JpaSpecificationExecutor<UserInfo> {

	UserInfo findByUserId(String userId);
	

	@Query("SELECT u from UserInfo u WHERE u.user = :user")
	UserInfo getByUserId(@Param("user") User user);

}
