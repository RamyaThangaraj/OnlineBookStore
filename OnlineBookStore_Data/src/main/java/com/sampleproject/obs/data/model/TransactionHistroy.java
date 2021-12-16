package com.sampleproject.obs.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction_histroy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistroy {
	
	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	private String id;
	private DateTime dateTime;
	private String transcationType;
	private String payments;
	private String amountInCurrency;
	private String stableCoin;
	private String balanceStableCoin;
	private String balanceAmountInCurrency;
	private String balanceHbars;
	
	
	
	private String nodeFee;
	@Column(name="network_fee")
	private String netWorkFee;
	@Column(name="transaction_fee")
	private String transactionfee;
	private String smartContractFee;
	private String totalFee;
	
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "security_id", nullable = false)
	private UserSecurity security;

}
