package com.sampleproject.obs.buyer.payload.request;

import org.joda.time.DateTime;

import lombok.Data;

@Data
public class BuyBookRequest {

	private String buyer_id;

	private String buyer_name;

	private String email;

	private long mobile_number;

	private String address;

	private String book_name;

	private String author;

	private String price;

	private int quantity;

	private String book_id;

	private DateTime createdTime;

	private String id;

}
