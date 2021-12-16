package com.sampleproject.obs.hedera.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface HederaWalletService {

	public ResponseEntity<?> createWallet() throws Exception;
	
}
