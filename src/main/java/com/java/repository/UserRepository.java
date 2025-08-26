package com.java.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.java.model.User;

/* Repositories handles all database operations using 
 * Spring Data JPA
 */


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // find user by email
    Optional<User> findByEmail(String email);

    // check if email already exists or not
    Boolean existsByEmail(String email);

    // find all active users 
    /* @Query annotion -> custom jpql queries for complex 
     * operations
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllActiveUsers();

    // find users with Gmail connected
    @Query("SELECT u FROM User u WHERE u.gmailConnected = true") 
    List<User> findAllUsersWithGmailConnected();

    // find active user connected by Gmail
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true")
    Optional<User> findByEmailActive(@Param("email") String email);
    
    /* jpql queries is used when derived queries are too limited
     * and when you perform complex queries
     */
}
