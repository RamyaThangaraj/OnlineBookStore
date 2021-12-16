package com.sampleproject.obs.data.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sampleproject.obs.data.model.TransactionHistroy;
import com.sampleproject.obs.data.model.User;


@Repository
public interface TranscationHistoryRepo extends JpaRepository<TransactionHistroy, String> {

	@Query("SELECT t FROM TransactionHistroy t WHERE t.user =:userId ORDER BY t.dateTime DESC")
	Page<TransactionHistroy> findByUserId(@Param("userId") User userId, Pageable pageable);

	@Query("SELECT t FROM TransactionHistroy t WHERE t.user =:userId ORDER BY t.dateTime ASC")
	List<TransactionHistroy> findByUserIdForGraph(@Param("userId") User userId);

	Page<TransactionHistroy> findByUser(User user, Pageable pageable);

	@Query("SELECT t FROM TransactionHistroy t WHERE t.user =:user ORDER BY t.dateTime DESC")
	List<TransactionHistroy> findByUser(@Param("user") User user);

	@Query("SELECT t FROM TransactionHistroy t ORDER BY t.dateTime DESC")
	List<TransactionHistroy> findByUser();

}
