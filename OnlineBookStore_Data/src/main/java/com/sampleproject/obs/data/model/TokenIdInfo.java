package com.sampleproject.obs.data.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Enumerated;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "token_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenIdInfo {

	public enum Status {
		ACTIVE, IN_ACTIVE
	}

	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	private String id;
	private String tokenId;
	@Enumerated(EnumType.STRING)
	private Status status;
	private String symbol;

}
