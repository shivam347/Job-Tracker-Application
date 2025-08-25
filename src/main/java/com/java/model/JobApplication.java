package com.java.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="job_applications")
@Data
@NoArgsConstructor
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Lazy means data is loaded on demand 
     * not immediately when parent entity is 
     * loaded
     */

     /*JoinColumn -> Specifies foreign key column */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @NotBlank
    @Size(max = 100)
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @NotBlank
    @Size(max = 100)
    @Column(name = "position", nullable = false)
    private String position;

    /* Enumerated tell jpa to store the 
     * enum constants as String in a database
     */

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name = "applied_date")
    private LocalDate appliedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private ApplicationSource source = ApplicationSource.MANUAL;

    @Size(max = 200)
    @Column(name = "job_url")
    private String jobUrl;

    @Column(name = "notes")
    @Size(max = 500)
    private String notes;

    @Column(name = "location")
    @Size(max = 100)
    private String location;

    @Column(name = "job_type")
    @Size(max = 50)
    private String jobType;  // Full-time, Part-time, Internship, Contract

    @Column(name="salary_range")
    private String salaryRange;

    /* Response received Date */
    @Column(name = "response_date")
    private LocalDate responseDate;

    /* Interview Date */
    @Column(name = "interview_date")
    private LocalDate interviewDate;

    @Column(name = "contact_person")
    @Size(max = 200)
    private String contactPerson;
    
    @Column(name = "contact_email")
    private String contactEmail;

    /* Gmail Message ID (if from email) */
    @Column(name = "gmail_message_id")
    private String gmailMessageId;

    /* Automatic time management */
    @CreationTimestamp
    @Column(name = "created_at" , updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    // Contructors

    public JobApplication(User user, String companyName, String position){
        this.user = user;
        this.companyName = companyName;
        this.position = position;
        this.appliedDate = LocalDate.now();
    }

    public JobApplication(User user, String companyName, String position, 
                         ApplicationStatus status, ApplicationSource source) {
        this.user = user;
        this.companyName = companyName;
        this.position = position;
        this.status = status;
        this.source = source;
        this.appliedDate = LocalDate.now();
    }


    // Enums
    public enum ApplicationStatus {
        APPLIED,
        UNDER_REVIEW,
        INTERVIEW_SCHEDULED,
        INTERVIEWED,
        REJECTED,
        OFFER_RECEIVED,
        OFFER_ACCEPTED,
        OFFER_DECLINED,
        WITHDRAWN
    }

    public enum ApplicationSource{
        MANUAL,
        GMAIL,
        INDEED,
        COMPANY_WEBSITE,
        OTHER
    }

    
 
    
}
