package com.sampleproject.obs.admin.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sampleproject.obs.admin.model.BookInfoRequest;
import com.sampleproject.obs.data.model.BookInfo;


@Service
public interface AdminService {

	ResponseEntity<?> addBook(BookInfoRequest bookInfo);

	ResponseEntity<?> updateBook(BookInfoRequest bookinforeq);

	ResponseEntity<?> deleteBook(String id);

	ResponseEntity<List<BookInfo>> showBookInfo();

	

}
