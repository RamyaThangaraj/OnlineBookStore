package com.sampleproject.obs.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfoRequest {
	
	private int amount;
	private int initialSupply;
	private int decimals;
	private String tokenName;
	private String tokenSymbol;
	private String operatorId;
	private String operatorKey;
	private String tokenId;
	private String fromSenderId;
	private String fromSenderKey;
	private String toAccountId;
	private String toAccountKey;
	private String loginUserName;
	private double amountInCurrency;
	
	private String userId;
	private String payments;

	private String transactionType;

}
