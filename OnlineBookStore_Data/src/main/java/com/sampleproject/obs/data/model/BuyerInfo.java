package com.sampleproject.obs.data.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "buyer_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerInfo {

	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	@Column(name = "buyer_id")
	private String buyer_id;

	private String buyer_name;

	private String email;

	private long mobile_number;

	private String address;

	private String book_name;

	private String author;

	private String price;

//	@OneToOne
//	@JoinColumn(name="quantity", referencedColumnName = "no_of_book_available")
	private int quantity;

	@Transient
	private String methodName;
	
	private String book_id;

	private DateTime createdTime;

	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="id")
	private User user;
}
