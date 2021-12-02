package com.sampleproject.obs.buyer.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sampleproject.obs.buyer.payload.request.BuyBookRequest;
import com.sampleproject.obs.data.model.BookInfo;

@Service
public interface BuyerService {

	ResponseEntity<List<BookInfo>> viewBookInfo();

	// ResponseEntity<BookInfo> viewByAuthor(String author);

	ResponseEntity<?> buyBook(BuyBookRequest buyBookRequest);

}
