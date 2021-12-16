package com.sampleproject.obs.buyer.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionFeeDto {
	private String transactionFeeId;
	private int amount;
	private String userId;
}
