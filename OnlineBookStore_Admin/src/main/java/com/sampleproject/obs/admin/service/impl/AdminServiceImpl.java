package com.sampleproject.obs.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sampleproject.obs.admin.model.BookInfoRequest;
import com.sampleproject.obs.admin.service.AdminService;
import com.sampleproject.obs.data.model.BookInfo;
import com.sampleproject.obs.data.repository.BookInfoRepository;

@Service
public class AdminServiceImpl implements AdminService{
	
	@Autowired
	BookInfoRepository bookInfoRepo;

	@Override
	public ResponseEntity<?> addBook(BookInfoRequest bookInfo) {
		BookInfo bookinfo = BookInfo.builder().bookname(bookInfo.getBookname()).author(bookInfo.getAuthor()).price(bookInfo.getPrice()).no_of_book_available(bookInfo.getNo_of_book_available()).build();
		bookinfo= bookInfoRepo.save(bookinfo);
		return ResponseEntity.ok(bookinfo);
	}

	@Override
	public ResponseEntity<?> updateBook(BookInfoRequest bookinforeq) {
		BookInfo bookinfo = bookInfoRepo.findById(bookinforeq.getId()).get();
		bookinfo= BookInfo.builder().id(bookinforeq.getId()).bookname(bookinforeq.getBookname()).author(bookinforeq.getAuthor()).price(bookinforeq.getPrice()).no_of_book_available(bookinforeq.getNo_of_book_available()).build();
		bookInfoRepo.save(bookinfo);
				return ResponseEntity.ok(bookinfo);
	}

	@Override
	public ResponseEntity<?> deleteBook(String id) {
		bookInfoRepo.findById(id);
		bookInfoRepo.deleteById(id);
		return ResponseEntity.ok("BOOK INFO DELETED");
	}

	@Override
	public ResponseEntity<List<BookInfo>> showBookInfo() {
		List<BookInfo> bookinformation=bookInfoRepo.findAll();
		return ResponseEntity.ok(bookinformation);
	}
	
}
