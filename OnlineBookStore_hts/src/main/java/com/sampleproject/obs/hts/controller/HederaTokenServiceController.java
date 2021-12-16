package com.sampleproject.obs.hts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sampleproject.obs.data.model.MintTokenDto;
import com.sampleproject.obs.data.payload.response.MessageResponse;
import com.sampleproject.obs.hts.request.CreateTokenDto;
import com.sampleproject.obs.hts.request.TokenBean;
import com.sampleproject.obs.hts.service.HederaTokenService;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/hts")
public class HederaTokenServiceController {
	
	@Autowired
	private HederaTokenService hederaTokenService;

	/* This API helps to get token balance for the given account.It takes input parameters as accountId */
	@RequestMapping(value = "/balance", method = RequestMethod.POST, produces = { "application/json" })
	public ResponseEntity<MessageResponse> balanceToken( @RequestBody String accountId ) throws Exception {
		return hederaTokenService.balanceToken( accountId );
	}
	
	/* This API helps to transfer token from user to admin and vice versa  */
	@RequestMapping(value = "/transfer", method = RequestMethod.POST)
	public ResponseEntity<?> transferToken(@RequestBody TokenBean tokenBean) throws Exception {
		return hederaTokenService.transferToken(tokenBean);
	}
	
	/* This API helps to create one token for entire application and make it created token as active */
	@RequestMapping(value = "/create", method = RequestMethod.POST, produces = { "application/json" })
	public ResponseEntity<?> createToken(@RequestBody CreateTokenDto createTokenDto) throws Exception {
		return hederaTokenService.createToken(createTokenDto);
	}
	
	/* This API helps to mint token */
	@RequestMapping(value = "/mint", method = RequestMethod.POST, produces = { "application/json" })
	public ResponseEntity<?> mintToken(@RequestBody MintTokenDto mintTokenDto) throws Exception {
		return hederaTokenService.mintToken(mintTokenDto);
	}
	
	/* This API helps to associate new user with token */
	@RequestMapping(value = "/associatkycuser", method = RequestMethod.POST, produces = { "application/json" })
	public ResponseEntity<MessageResponse> associatKycUser(@RequestBody TokenBean tokenBean) throws Exception {
		return hederaTokenService.associatKycUser(tokenBean);
	}

}
