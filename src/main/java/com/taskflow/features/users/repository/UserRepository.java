package com.taskflow.features.users.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taskflow.features.users.model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	List<User> findByUsernameContainingIgnoreCase(String query);
	@Query("SELECT u FROM User u WHERE " +
	           "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
	           "AND u.id NOT IN (SELECT m.id FROM Project p JOIN p.members m WHERE p.id = :projectId) " +
	           "AND u.id != (SELECT p.owner.id FROM Project p WHERE p.id = :projectId)")
	    List<User> findUsersToInvite(@Param("query") String query, @Param("projectId") Long projectId);
}

