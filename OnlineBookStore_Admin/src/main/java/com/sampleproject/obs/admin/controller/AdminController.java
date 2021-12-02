package com.sampleproject.obs.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sampleproject.obs.admin.model.BookInfoRequest;
import com.sampleproject.obs.admin.service.AdminService;
import com.sampleproject.obs.data.model.BookInfo;

@CrossOrigin
@RestController
@RequestMapping(value = "/obs")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	@Autowired
	AdminService adminService;

	@PostMapping("/saveBook")
	public ResponseEntity<?> addBookInfo(@RequestBody BookInfoRequest bookInfo) {
		return adminService.addBook(bookInfo);
	}
	
	@PutMapping("/updateBook")
	public ResponseEntity<?> updatBookInfo(@RequestBody BookInfoRequest bookinforeq){
		return adminService.updateBook(bookinforeq);
	}
	
	@DeleteMapping("/deleteBook/{id}")
	public ResponseEntity<?> deleteBookInfo(@PathVariable(value = "id") String id){
		return adminService.deleteBook(id);
	}
	
	@GetMapping("/showBook")
	public ResponseEntity<List<BookInfo>>showBookInfo(){
		return adminService.showBookInfo();
	}
	
	//search by author
	//search by bookname

}
