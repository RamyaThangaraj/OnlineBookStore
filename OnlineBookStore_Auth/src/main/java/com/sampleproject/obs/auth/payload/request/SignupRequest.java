package com.sampleproject.obs.auth.payload.request;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SignupRequest {
	
private String name;
	//"mobileNumber" "address" "email" "password" "role" ""
	@NotNull
	private long mobileNumber;
	
	@NotNull
	@Email
	private String email;
	
	@NotNull
	private String address;
	
	@NotNull
	private String password;
	
	@NotNull
	private Set<String> role;
	

}
