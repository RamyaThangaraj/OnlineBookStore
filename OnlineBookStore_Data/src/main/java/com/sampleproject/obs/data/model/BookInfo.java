package com.sampleproject.obs.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(	name = "book_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookInfo {
	
	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	@Column(name = "book_id")
	private String id;
	
	private String bookname;
	
	private String author;
	
	private String price;
	
	//@Column(name="no_of_book_available")
	private int no_of_book_available;

}
