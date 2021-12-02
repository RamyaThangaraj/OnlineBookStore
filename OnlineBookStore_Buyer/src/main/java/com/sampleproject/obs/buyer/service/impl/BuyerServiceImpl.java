package com.sampleproject.obs.buyer.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sampleproject.obs.buyer.payload.request.BuyBookRequest;
import com.sampleproject.obs.buyer.service.BuyerService;
import com.sampleproject.obs.data.model.BookInfo;
import com.sampleproject.obs.data.model.BuyerInfo;
import com.sampleproject.obs.data.model.User;
import com.sampleproject.obs.data.repository.BookInfoRepository;
import com.sampleproject.obs.data.repository.BuyerInfoRepository;
import com.sampleproject.obs.data.repository.UserRepository;

@Service
public class BuyerServiceImpl implements BuyerService {

	@Autowired
	BuyerInfoRepository buyerInfoRepository;

	@Autowired
	BookInfoRepository bookInfoRepository;

	@Autowired
	Environment env;

	@Autowired
	UserRepository userRepository;

	@Override
	public ResponseEntity<List<BookInfo>> viewBookInfo() {
		List<BookInfo> bookinfo = bookInfoRepository.findAll();
		return ResponseEntity.ok(bookinfo);
	}

	@Override
	public ResponseEntity<?> buyBook(BuyBookRequest buyBookRequest) {
		User user = userRepository.getById(buyBookRequest.getBuyer_id());
		BookInfo book = bookInfoRepository.findByBookNameAndAuthor(buyBookRequest.getBook_name(),
				buyBookRequest.getAuthor());
		int bookCount = book.getNo_of_book_available() - buyBookRequest.getQuantity();
//		bookCount--;
		bookInfoRepository.updateBookCount(bookCount, buyBookRequest.getBook_name(), buyBookRequest.getAuthor());

		BuyerInfo buyerInfo = BuyerInfo.builder().buyer_id(buyBookRequest.getBuyer_id())
				.buyer_name(buyBookRequest.getBuyer_name()).email(buyBookRequest.getEmail())
				.mobile_number(buyBookRequest.getMobile_number()).address(buyBookRequest.getAddress())
				.book_name(buyBookRequest.getBook_name()).book_id(buyBookRequest.getBook_id())
				.author(buyBookRequest.getAuthor()).price(buyBookRequest.getPrice())
				.quantity(buyBookRequest.getQuantity()).createdTime(buyBookRequest.getCreatedTime()).user(user).build();

		return ResponseEntity.ok(buyerInfo);
	}

}
/* */
