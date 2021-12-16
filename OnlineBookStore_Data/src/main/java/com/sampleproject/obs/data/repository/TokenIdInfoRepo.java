package com.sampleproject.obs.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sampleproject.obs.data.model.TokenIdInfo;

@Repository
public interface TokenIdInfoRepo extends JpaRepository<TokenIdInfo, String>{

	@Query("SELECT t FROM TokenIdInfo t ORDER BY t.id DESC")
	List<TokenIdInfo> findAllByDesc();
	//@Query("SELECT t FROM TokenIdInfo t where t.status=:status")
	@Query("SELECT t FROM TokenIdInfo t where t.status=:status")
	TokenIdInfo findByStatus(@Param("status")TokenIdInfo.Status status);
	
	@Query("SELECT t FROM TokenIdInfo t where id=:id")
	TokenIdInfo getById(@Param("id") String id);
	
	@Query("SELECT t FROM TokenIdInfo t ORDER BY t.status ASC")
	List<TokenIdInfo> findAll();
}
