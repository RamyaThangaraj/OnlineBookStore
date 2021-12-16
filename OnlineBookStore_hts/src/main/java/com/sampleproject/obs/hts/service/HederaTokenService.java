package com.sampleproject.obs.hts.service;

import org.springframework.http.ResponseEntity;

import com.sampleproject.obs.data.model.MintTokenDto;
import com.sampleproject.obs.data.payload.response.MessageResponse;
import com.sampleproject.obs.hts.request.CreateTokenDto;
import com.sampleproject.obs.hts.request.TokenBean;

public interface HederaTokenService {
	
	public ResponseEntity<MessageResponse> balanceToken( String accountId );

	public ResponseEntity<?> transferToken(TokenBean tokenBean);

	public ResponseEntity<?> createToken(CreateTokenDto tokenBean)  throws Exception;

	public ResponseEntity<MessageResponse> associatKycUser(TokenBean tokenBean);

	public ResponseEntity<?> mintToken(MintTokenDto mintTokenDto) throws Exception;
	
}
