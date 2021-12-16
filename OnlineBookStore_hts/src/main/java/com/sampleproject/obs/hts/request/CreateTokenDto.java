package com.sampleproject.obs.hts.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTokenDto {
	
	private String adminOperatorId;
	private String adminOperatorkey;
	private String operatorId;
	private String operatorkey;
	private String tokenName;
	private String tokenSymbol;
	private String initialSupply;

}
