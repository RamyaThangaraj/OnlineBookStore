package com.sampleproject.obs.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MintTokenDto {

	private String operatorId;
	private String operatorKey;
	private int amount;
	private String tokenId;
	private String userId;
}
