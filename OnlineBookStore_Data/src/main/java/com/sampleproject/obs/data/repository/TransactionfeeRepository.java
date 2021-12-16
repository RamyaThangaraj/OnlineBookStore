package com.sampleproject.obs.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sampleproject.obs.data.model.TransactionFee;

public interface TransactionfeeRepository extends JpaRepository<TransactionFee, String>{

}
