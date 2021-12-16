package com.sampleproject.obs.hcs.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDto {
	
	private String fileId;
	private String tokenId;
	private String accountId;
	private String topicId;

}
