package com.sampleproject.obs.data.request;

import com.sampleproject.obs.data.model.User;
import com.sampleproject.obs.data.model.UserSecurity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionHistroyRequest {

	private String id;
	private String dateTime;
	private String transcationType;
	private String payments;
	private String amountInCurrency;
	private String stableCoin;
	private String balanceStableCoin;
	private String balanceAmountInCurrency;
	private String nodeFee;
	private String netWorkFee;
	private String transactionfee;
	private String totalFee;
	private User user;
	private UserSecurity security;
}
