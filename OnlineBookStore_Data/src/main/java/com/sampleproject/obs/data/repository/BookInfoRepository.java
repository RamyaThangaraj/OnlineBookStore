package com.sampleproject.obs.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sampleproject.obs.data.model.BookInfo;

@Repository
public interface BookInfoRepository extends JpaRepository<BookInfo, String> {

	Optional<BookInfo> findById(String id);

	List<BookInfo> findAll();

	// @Query("SELECT u FROM UserInfo u ORDER BY u.companyName DESC")
	// Page<UserInfo> getAllCompany(Pageable pageable);
	BookInfo findByAuthor(String author);

	// int deleteByUser(User byId);void deleteById(ID id);
	void deleteById(String id);

	@Query(value = "Select * from book_info b where b.bookname=:bookName and b.author=:author", nativeQuery = true)
	BookInfo findByBookNameAndAuthor(@Param("bookName") String book_name, @Param("author") String author);

	@Modifying
	@Transactional
	@Query(value = "update book_info  set no_of_book_available=:count where bookname=:bookName and author=:author ", nativeQuery = true)
	void updateBookCount(@Param("count") int bookCount, @Param("bookName") String bookName,
			@Param("author") String author);

//	BookInfo getBookId(String id);
//	BookInfo findByQuantity(String no_of_book_available);

//	BookInfo getBookById(String id);

//	@Query("SELECT u from UserInfo u WHERE u.user = :user")
//	UserInfo getByUserId(@Param("user") User user);
}
