package com.sampleproject.obs.hedera.utils;

import java.util.concurrent.TimeoutException;

import com.hedera.hashgraph.sdk.AccountBalance;
import com.hedera.hashgraph.sdk.AccountBalanceQuery;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenMintTransaction;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;

public class HederaUtil {
	
	 public static void main(String[] args) throws TimeoutException, PrecheckStatusException, ReceiptStatusException {
		 HederaUtil hu = new HederaUtil();
		 AccountId accountId = AccountId.fromString("0.0.19142548");
		hu.accountTokenBalnce(accountId);
//		hu.mintToken();
	}
	 
	 public  void accountTokenBalnce(AccountId accountId) throws TimeoutException, PrecheckStatusException {
		 Client client = setOperator();
		//Create the query
		 AccountBalanceQuery query = new AccountBalanceQuery()
		     .setAccountId(accountId);

		 //Sign with the operator private key and submit to a Hedera network
		 AccountBalance tokenBalance = query.execute(client);

		 System.out.println("The token balance(s) for this account: " +tokenBalance.hbars);
	 }
	 
	 public Client setOperator() {
		 Client client = Client.forTestnet() ;
		 String accountId = "0.0.15817037";
		 String pKey = "302e020100300506032b657004220420779050b96b36e470b05d46472d02a9cd4a62b8ca3e0447b8af2d6973bc9e8405";
		client= client.setOperator(AccountId.fromString(accountId), PrivateKey.fromString(pKey));
		 return client;
	 }
	 
	public void mintToken() throws TimeoutException, PrecheckStatusException, ReceiptStatusException {
		 Client client = setOperator();
		 String operatorPKey = "302e020100300506032b657004220420779050b96b36e470b05d46472d02a9cd4a62b8ca3e0447b8af2d6973bc9e8405";
		 PrivateKey privateKey = PrivateKey.fromString(operatorPKey);
		TokenId tokenId = TokenId.fromString("0.0.19140102");
		TokenMintTransaction transaction = new TokenMintTransaction()
			     .setTokenId(tokenId)
			     .setAmount(1000);

			//Freeze the unsigned transaction, sign with the supply private key of the token, submit the transaction to a Hedera network
			TransactionResponse txResponse = transaction.freezeWith(client).sign(privateKey).execute(client);

			//Request the receipt of the transaction
			TransactionReceipt receipt = txResponse.getReceipt(client);

			//Obtain the transaction consensus status
			Status transactionStatus = receipt.status;

			System.out.println("The transaction consensus status is " +transactionStatus);

	}
	
	}
