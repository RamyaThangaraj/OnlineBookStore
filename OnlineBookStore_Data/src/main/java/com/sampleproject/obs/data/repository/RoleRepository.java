package com.sampleproject.obs.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sampleproject.obs.data.model.ERole;
import com.sampleproject.obs.data.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(ERole name);

	Boolean existsByName(ERole name);
}
