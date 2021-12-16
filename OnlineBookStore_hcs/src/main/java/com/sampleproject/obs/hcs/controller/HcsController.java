package com.sampleproject.obs.hcs.controller;

import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import com.sampleproject.obs.hcs.service.HcsTemplate;



@CrossOrigin
@RestController
@RequestMapping(value = "/api/hcs")
public class HcsController {

	@Autowired
	HcsTemplate hcsTemplate;

	/* This Api helps to publish in the mirror node for user /publishmirronode/user */
	@PostMapping("/publishmirronode/user")
	public ResponseEntity<?> publishUser(@RequestBody String inputByte) throws TimeoutException, PrecheckStatusException, ReceiptStatusException {
		return hcsTemplate.publisToMirrorNode(inputByte);
	}
	
	/* @Autowired
	HcsTemplate hcsTemplate;

	/* This Api helps to publish message in the mirror node 
	@PostMapping("/publishmirronode")
	@PreAuthorize("hasRole('COMPANY')  or hasRole('ADMIN')")
	public ResponseEntity<?> publish(@RequestBody String inputByte) {
		return hcsTemplate.publisToMirrorNode(inputByte);
	}

	/* This Api helps to publish in the mirror node for user 
	@PostMapping("/publishmirronode/user")
	public ResponseEntity<?> publishUser(@RequestBody String inputByte) {
		return hcsTemplate.publisToMirrorNode(inputByte);
	}
 */
	
}
