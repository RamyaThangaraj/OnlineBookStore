package com.sampleproject.obs.hedera.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletCreateResponse {
	
	private String account;
	private String privatekey;
	private long shard;
	private long real;
	private long accoundId;
	private String publickey;

}
