package com.unipapers.backend.Common.Repositories;

import com.unipapers.backend.Common.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
	Optional<User> findByPublicId(String publicId);
	Optional<User> findByEmail(String email);
	Optional<User> findByStudentNumber(Long studentNumber);

	@Modifying
	@Query("update User u set u.semester = :semester")
	void updateAllSemesters(Integer semester);

	Optional<User> findByEmailOrStudentNumber(String identifier, Long studentNum);
}
