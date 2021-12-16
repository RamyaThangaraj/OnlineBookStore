package com.sampleproject.obs.hcs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TopicCreateTransaction;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessageSubmitTransaction;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.TransferTransaction;

public class CreateTopic {
	private static final Logger logger = LoggerFactory.getLogger(CreateTopic.class);

	public static void main(String[] args) throws Exception {
//		Ed25519PrivateKey operatorKey = Ed25519PrivateKey.fromString(
//				"302e020100300506032b657004220420779050b96b36e470b05d46472d02a9cd4a62b8ca3e0447b8af2d6973bc9e8405");
//		AccountId operatorId = AccountId.fromString("0.0.15817037");
//
//		HCSCore hcsCore = HCSCore.INSTANCE.singletonInstance("0").withOperatorAccountId(operatorId)
//				.withOperatorKey(operatorKey);
//
//		// create topics on HCS
//		CreateHCSTopic createHCSTopic = new CreateHCSTopic(hcsCore);
//		ConsensusTopicId topicId = createHCSTopic.execute();
//		System.out.println(topicId.toString());
//
//	}
		
//		  AccountId myAccountId = AccountId.fromString(Dotenv.load().get("MY_ACCOUNT_ID"));
//	    PrivateKey myPrivateKey = PrivateKey.fromString(Dotenv.load().get("MY_PRIVATE_KEY"));
//   // Create your Hedera testnet client
//    Client client = Client.forTestnet();
//    client.setOperator(myAccountId, myPrivateKey);
		  
		Client client=Client.forTestnet();
		client.setOperator(AccountId.fromString( "0.0.15817037"),PrivateKey.fromString("302e020100300506032b657004220420779050b96b36e470b05d46472d02a9cd4a62b8ca3e0447b8af2d6973bc9e8405") );
		System.out.println("test");
		
		TopicCreateTransaction transaction = new TopicCreateTransaction();
		//Sign with the client operator private key and submit the transaction to a Hedera network
		TransactionResponse txResponse = transaction.execute(client);
		//Request the receipt of the transaction
		TransactionReceipt receipt = txResponse.getReceipt(client);
		//Get the topic ID
		TopicId newTopicId = receipt.topicId;
		System.out.println("The new topic ID is " + newTopicId);
		
		
		
		//Create the transaction
				TopicMessageSubmitTransaction transaction1 = new TopicMessageSubmitTransaction()
				    .setTopicId(newTopicId)
				    .setMessage("hello, HCS! ");
				//Sign with the client operator key and submit transaction to a Hedera network, get transaction ID
				TransactionResponse txResponse1 = transaction1.execute(client);
				//Request the receipt of the transaction
				TransactionReceipt receipt1 = txResponse1.getReceipt(client);
				//Get the transaction consensus status
				Status transactionStatus =  receipt1.status;
				System.out.println("The transaction consensus status is " +transactionStatus);
				//v2.0.0
				
//				// Create a transaction to transfer 100 hbars
//				TransferTransaction transaction11 = new TransferTransaction()
//				     .addHbarTransfer(operator.id, new Hbar(-10))
//				     .addHbarTransfer(newAccountId, new Hbar(10));
//
//				//Submit the transaction to a Hedera network
//				TransactionResponse txResponse11 = transaction11.execute(client);
//
//				//Request the receipt of the transaction
//				TransactionReceipt receipt11 = txResponse11.getReceipt(client);
//
//				//Get the transaction consensus status
//				Status transactionStatus1 = receipt11.status;
//
//				System.out.println("The transaction consensus status is " +transactionStatus1);
//
//				//Version 2.0.0
}

}