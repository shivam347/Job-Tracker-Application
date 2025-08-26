package com.java.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.java.model.JobApplication;
import com.java.model.User;
import com.java.model.JobApplication.ApplicationSource;
import com.java.model.JobApplication.ApplicationStatus;

/* Spring data Jpa automatically
 * creates queries based on method name
 * 
 * @Query for complex operations
 * @param to bind method parameters to query parameters
 */
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long>{

    // Get all applications for a user, ordered by creation data
    List<JobApplication> findByUserOrderedByCreatedAtDesc(User user);

    // Get application by status
    List<JobApplication> findByUserAndStatus(User user, ApplicationStatus status);

    // Get application by source
    List<JobApplication> findByUserAndSource(User user, ApplicationSource source);

    // Get application in date range 
    @Query("SELECT ja FROM JobApplication ja WHERE ja.user = :user AND ja.appliedDate BETWEEN :startDate AND :endDate ORDER BY ja.appliedDate DESC")
    List<JobApplication> findByUserAndAppliedDate(@Param("user") User user, @Param("startDate") LocalDate startDate,
    @Param("endDate") LocalDate endDate);

    // Count application for user 
    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.user = :user")
    Long countApplicationByUser(@Param("user") User user);

    // Count Application By status 
    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.user = :user And ja.status = :status")
    Long countApplicationByStatus(@Param("user") User user, @Param("status") ApplicationStatus status);

    // Search By company name
    @Query("SELECT ja FROM JobApplication ja WHERE ja.user = :user And LOWER(ja.company) AND LOWER(CONCAT('%', :company, '%'))")
    List<JobApplication> findByUserCompanyName(@Param("user") User user, @Param("company") String company);

    // Search job application by position
    @Query("SELECT ja FROM JobApplication ja WHERE ja.user = :user AND LOWER(ja.position) AND LIKE LOWER(CONCAT('%', position, '%'))")
    List<JobApplication> findByUserPosition(@Param("user") User user, @Param("position") String position);


    // find by gmail message ID
    Optional<JobApplication> findByUserAndMessageID(User user, String gmailMessageId);

    /* Get stats by status means how may application are appplied , how many get offer or interview etc */
    @Query("SELECT DISTINCT ja.status , COUNT(ja) FROM JobApplication ja WHERE ja.user = :user GROUP BY ja.status")
    List<Object[]> getStatusStatsByUser(@Param("user") User user);


    /* Get statistics by source */
    @Query("SELECT DISTINCT ja.source, COUNT(ja) FROM JobApplication ja WHERE ja.user = :user GROUP BY ja.source")
    List<Object[]> getSourceStatsByUser(@Param("user") User user);


    // Get upcoming interviews
    @Query("SELECT ja FROM JobApplication ja WHERE ja.user = :user AND ja.interviewDate IS NOT NULL AND ja.interviewDate >= CURRENT_DATE ORDER BY ja.interviewDate ASC")
    List<JobApplication> findUpcomingInterviewByUser(@Param("user") User user);


    // Get Pending offers 
    @Query("SELECT ja FROM JobApplication ja WHERE ja.user = :user AND ja.status IN('OFFER_RECEIVED') ORDER BY ja.updatedAt DESC")
    List<JobApplication> findPendingOffersByUser(@Param("user") User user);

















}
