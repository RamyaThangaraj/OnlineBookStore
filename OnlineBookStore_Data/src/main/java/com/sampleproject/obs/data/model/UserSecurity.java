package com.sampleproject.obs.data.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "user_security")
@Data
@Builder 
@NoArgsConstructor
@AllArgsConstructor
public class UserSecurity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "user_id")
	private String userID;

	@MapsId
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@NotNull
	private long accountShard;

	@NotNull
	private long accountRealm;

	@NotNull
	private long accountNum;

	@NotBlank
	private String userPubKey;

	@NotBlank
	private String userPrivateKey;

	private String accountBalance;

	@Transient
	private String methodName;

}
