package com.sampleproject.obs.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sampleproject.obs.data.model.BuyerInfo;

@Repository
public interface BuyerInfoRepository extends JpaRepository<BuyerInfo, String> {
	// @Query("UPDATE Admin SET firstname = :firstname, lastname = :lastname, login
	// = :login, superAdmin = :superAdmin, preferenceAdmin = :preferenceAdmin,
//	address =  :address, zipCode = :zipCode, city = :city, country = :country, email = :email, profile = :profile, postLoginUrl = :postLoginUrl WHERE id = :id")
//	public void update(@Param("firstname") String firstname, @Param("lastname") String lastname, @Param("login") String login, @Param("superAdmin") boolean superAdmin, 
//			@Param("preferenceAdmin") boolean preferenceAdmin, @Param("address") String address, @Param("zipCode") String zipCode, @Param("city") String city, 
	// @Param("country") String country, @Param("email") String email,
	// @Param("profile") String profile, @Param("postLoginUrl") String postLoginUrl,
	// @Param("id") Long id);

	// @Query("UPDATE book_info SET no_of_book_available =:(no_of_book_available -
	// quantity) WHERE buyer_id =: id ")

	// @Query("SELECT R.quantity , R.no_of_book_available - COUNT (A.buyer_id) AS
	// no_of_book_available FROM book_info R LEFT JOIN buyer_info ON R.quantity =
	// A.quantity GROUP BY R.quantity , R.no_of_book_available")
//	@Query(value="SELECT b.quantity  ")
//	Optional<BuyerInfo> findByBuyerId(String buyer_id);

}
