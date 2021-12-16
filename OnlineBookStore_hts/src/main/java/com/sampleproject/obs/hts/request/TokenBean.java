package com.sampleproject.obs.hts.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenBean {
	
	private int amount;
	private int initialSupply;
	private int decimals;
	private String tokenName;
	private String tokenSymbol;
	private String treasuryAccountId;
	private String adminKey;
	private String kycKey;
	private String freezeKey;
	private String wipeKey;
	private String supplyKey;
	private String operatorId;
	private String operatorKey;
	private String tokenId;
	private String fromSenderId;
	private String fromSenderKey;
	private String toAccountId;
	private String toAccountKey;
	private String transactionType;
	private String payments;
	private String amountInCurrency;
	private String stableCoin;
	private String balanceStableCoin;
	private String balanceHbars;
	private String operatorAmountInCurrency;
	private String operatorStableCoin;
	private int amountOperator;
	private String userId;
	private String trade;
	private String contractId;
	
}
