package com.sampleproject.obs.hedera.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sampleproject.obs.hedera.service.HederaWalletService;

//import com.sampleproject.obs.hedera.service.HederaWalletService;


@CrossOrigin
@RestController
@RequestMapping(value = "/api/hedera")
public class HederaWalletController {
	
  	@Autowired
	HederaWalletService hederaWalletService;
	
	// This Api helps to create wallet in hedera network
	@RequestMapping(value = "/createwallet", method = RequestMethod.POST)
	public ResponseEntity<?> createWallet(  ) throws Exception {
		return hederaWalletService.createWallet();
	}
	

}
