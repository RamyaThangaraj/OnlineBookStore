package com.sampleproject.obs.hcs.service;

import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessageSubmitTransaction;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.sampleproject.obs.data.payload.response.MessageResponse;
import com.hedera.hashgraph.sdk.PrivateKey;
@Service
public class HcsTemplate {
	
	@Autowired
	Environment env;

	public ResponseEntity<?> publisToMirrorNode(String inputByte)
	throws TimeoutException, PrecheckStatusException, ReceiptStatusException {
	Client client = setOperator();
	TopicId topicId = TopicId.fromString(env.getProperty("topic.id"));

	//Create the transaction
	TopicMessageSubmitTransaction transaction = new TopicMessageSubmitTransaction()
	.setTopicId(topicId)
	.setMessage(inputByte);
	//Sign with the client operator key and submit transaction to a Hedera network, get transaction ID
	TransactionResponse txResponse = transaction.execute(client);
	//Request the receipt of the transaction
	TransactionReceipt receipt = txResponse.getReceipt(client);
	//Get the transaction consensus status
	Status transactionStatus = receipt.status;
	System.out.println("The transaction consensus status is: " +transactionStatus);

	// return ResponseEntity.ok(new MessageResponse("Message Published into HCS",HttpStatus.OK.value(),""));
	return ResponseEntity
	.ok(new MessageResponse(HttpStatus.OK.value(), env.getProperty("publish.message.success"),""));
	}

	public Client setOperator() {
	Client client = null;
	String operatorId = env.getProperty("operator.id");
	String operatorKey = env.getProperty("operator.privatekey");
	client=Client.forTestnet();
	client.setOperator(AccountId.fromString( operatorId ),PrivateKey.fromString(operatorKey));

	return client;
	}

}
