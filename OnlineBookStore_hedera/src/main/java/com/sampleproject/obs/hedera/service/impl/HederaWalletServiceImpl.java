package com.sampleproject.obs.hedera.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hedera.hashgraph.sdk.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.sampleproject.obs.data.payload.response.MessageResponse;
import com.sampleproject.obs.hedera.payload.response.WalletCreateResponse;
import com.sampleproject.obs.hedera.service.ExampleHelper;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.env.Environment;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;

//import com.hedera.hashgraph.sdk.AccountCreateTransaction;
//import com.hedera.hashgraph.sdk.AccountId;
//import com.hedera.hashgraph.sdk.Client;
//import com.hedera.hashgraph.sdk.Hbar;
//import com.hedera.hashgraph.sdk.PrivateKey;
//import com.hedera.hashgraph.sdk.TransactionReceipt;
//import com.hedera.hashgraph.sdk.TransactionResponse;
//import com.sampleproject.obs.data.payload.response.MessageResponse;
//import com.sampleproject.obs.hedera.payload.response.WalletCreateResponse;
//import com.sampleproject.obs.hedera.service.ExampleHelper;
import com.sampleproject.obs.hedera.service.HederaWalletService;

@Service
public class HederaWalletServiceImpl implements HederaWalletService {
	
	@Autowired
	Environment env;

	@Value("${network}")
	private String network;

//	/*
//	 * createWallet method is used to create wallet from Hedera nework
//	 */
	@Override
	public ResponseEntity<?> createWallet() {
		Client client = setOperator();
		WalletCreateResponse walletCreateResponse = new WalletCreateResponse();
		try {
			PrivateKey newKey = PrivateKey.generate();
			TransactionResponse txId = new AccountCreateTransaction().setKey(newKey.getPublicKey()).setInitialBalance(Hbar.from(1000)).execute(client);

			TransactionReceipt receipt = txId.getReceipt(client);

			AccountId newAccountId = receipt.accountId;
			String account = newAccountId.shard + "." + newAccountId.realm + "." + newAccountId.num;
			walletCreateResponse = WalletCreateResponse.builder().accoundId(newAccountId.num)
					.privatekey(newKey.toString()).shard(newAccountId.shard).real(newAccountId.realm).account(account)
					.publickey(newKey.getPublicKey().toString()).build();

			return ResponseEntity.ok(new MessageResponse(HttpStatus.OK.value(),
					env.getProperty("wallet.created.success"), walletCreateResponse));
		} catch (Exception e) {

			return ResponseEntity
					.ok(new MessageResponse(HttpStatus.PARTIAL_CONTENT.value(), e.getMessage(), walletCreateResponse));
		}

	}

	private Client setOperator() {
		Client client = null;

		String operatorId=env.getProperty("operator.id");
		
		String operatorKey=env.getProperty("operator.privatekey");
		
		if (network.equalsIgnoreCase("testnet")) {

			client = Client.forTestnet();
			client.setOperator(ExampleHelper.getOperatorId(operatorId), ExampleHelper.getOperatorKey(operatorKey));

		} else if (network.equalsIgnoreCase("mainnet")) {
			client = Client.forMainnet();
			client.setOperator(ExampleHelper.getOperatorId(operatorId), ExampleHelper.getOperatorKey(operatorKey));

		}
		return client;
	}

}
