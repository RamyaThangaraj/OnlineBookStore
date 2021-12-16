package com.sampleproject.obs.data.request;

import lombok.Data;

@Data
public class CreateTokenRequest {
	
	private String tokenName;
	private String tokenSymbol;
	private String initialSupply;
	private String userId;

}
