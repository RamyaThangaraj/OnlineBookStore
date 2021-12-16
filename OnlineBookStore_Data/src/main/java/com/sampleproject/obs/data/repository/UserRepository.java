package com.sampleproject.obs.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sampleproject.obs.data.model.Role;
import com.sampleproject.obs.data.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	
	Optional<User> findByUsername(String username);

	Boolean existsByEmail(String email);
	
	User findByEmail(String email);
	
	Boolean existsByUsername(String username);
	
	Optional<User> getById(String id);
	
	List<User> findAllByRoles(Role role);

}
