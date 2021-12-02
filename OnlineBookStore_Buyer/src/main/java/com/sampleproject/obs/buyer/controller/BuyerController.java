package com.sampleproject.obs.buyer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sampleproject.obs.buyer.payload.request.BuyBookRequest;
import com.sampleproject.obs.buyer.service.BuyerService;
import com.sampleproject.obs.data.model.BookInfo;

@CrossOrigin
@RestController
@RequestMapping(value = "/obs")
public class BuyerController {

	// search book from bookinfo✔
	// buy book✔
	// search book by name
	// search book by author✔

	@Autowired
	BuyerService buyerService;

	@GetMapping("/viewBook")
	public ResponseEntity<List<BookInfo>> viewBookInfo() {
		return buyerService.viewBookInfo();
	}

//	@GetMapping("/viewBookByAuthor")
//	public ResponseEntity<BookInfo> viewByAuthor(@PathVariable(value = "author") String author) {
//		return buyerService.viewByAuthor(author);
//
	/*
	 * @PostMapping("/saveBook") public ResponseEntity<?> addBookInfo(@RequestBody
	 * BookInfoRequest bookInfo) { return adminService.addBook(bookInfo); }
	 */
//	}

	@PostMapping("/buyBook")
	public ResponseEntity<?> buyBook(@RequestBody BuyBookRequest buyBookRequest) {
		return buyerService.buyBook(buyBookRequest);
	}

}
