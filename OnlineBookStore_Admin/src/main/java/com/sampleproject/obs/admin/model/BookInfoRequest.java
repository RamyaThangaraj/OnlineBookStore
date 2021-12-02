package com.sampleproject.obs.admin.model;

import lombok.Data;

@Data
public class BookInfoRequest {

	private String id;
	
	private String bookname;
	
	private String author;
	
	private String price;
	
	private int no_of_book_available;

}
