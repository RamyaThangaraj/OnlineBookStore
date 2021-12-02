package com.sampleproject.obs.auth.payload.response;

import java.util.List;

import com.sampleproject.obs.data.model.UserInfo;

import lombok.Data;

@Data
public class JwtResponse {

	private String accessToken;
	private String tokenType = "Bearer";
	private String id;
	private String username;
	private String email;
	private List<String> roles;
	private String refreshToken;

	private UserInfo info;

	public JwtResponse(String accessToken, String id, String username, String email, List<String> roles,
			UserInfo info) {
		this.accessToken = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
		this.info = info;
	}

}
