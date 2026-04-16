package com.unipapers.backend.Common.Repositories;

import com.unipapers.backend.Common.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
	Optional<User> findByPublicId(String publicId);
}
