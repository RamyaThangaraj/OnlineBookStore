package com.sampleproject.obs.data.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MessageResponse {

	private int status;
	private String message;
	private Object response;

	public MessageResponse(String string, int status, Object response) {
		this.message = string;
		this.status = status;
		this.response = response;
	}

	public MessageResponse(int status, String string) {
		this.message = string;
		this.status = status;
	}

}
