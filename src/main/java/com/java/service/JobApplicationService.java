package com.java.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.model.JobApplication;
import com.java.model.User;
import com.java.repository.JobApplicationRepository;

/* Service layer sits between controller(api/ui) and repository(db) 
 * 
 * @Service -> mark spring as service component so that spring 
 * automatically detects and manages it as a bean
 * 
 * @Autowired -> Inject the jobApplication repository where all database queries
 * are defined 
 * 
 * @Method -> provides CRUD Operations + filtering and searching 
 * 
 */

@Service
public class JobApplicationService {

    // Inject the repository so that service can iteracting with DB
    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    // Method to get all job application sorted by created date
    public List<JobApplication> getAllJobApplicationUser(User user) {

        return jobApplicationRepository.findByUserOrderedByCreatedAtDesc(user);
    }

    // Method to fetch an application by id but returns only if it belongs to a user
    // (security check)
    public Optional<JobApplication> getJobApplicationById(Long id, User user) {

        return jobApplicationRepository.findById(id)
                .filter(app -> app.getId().equals(user.getId()));
    }

    // create a jobApplication and set it to a user with applied date, if null
    // default date is today and save it to db
    public JobApplication createApplication(JobApplication application, User user) {

        application.setUser(user);

        if (application.getAppliedDate() == null) {
            application.setAppliedDate(LocalDate.now());
        }

        return jobApplicationRepository.save(application);
    }

    /*
     * Updating an existing application only if it belongs to the user
     */
    public JobApplication updateApplication(Long id, JobApplication application, User user) {

        Optional<JobApplication> existingApplication = getJobApplicationById(id, user);

        if (existingApplication.isPresent()) {
            JobApplication app = existingApplication.get();

            app.setCompanyName(application.getCompanyName());
            app.setPosition(application.getPosition());
            app.setStatus(application.getStatus());
            app.setAppliedDate(application.getAppliedDate());
            app.setJobUrl(application.getJobUrl());
            app.setLocation(application.getLocation());
            app.setJobType(application.getJobType());
            app.setSalaryRange(application.getSalaryRange());
            app.setNotes(application.getNotes());
            app.setContactPerson(application.getContactPerson());
            app.setContactEmail(application.getContactEmail());
            app.setInterviewDate(application.getInterviewDate());
            app.setResponseDate(application.getResponseDate());

            return jobApplicationRepository.save(app);
        }

        throw new RuntimeException("Application not found or access is denied");
    }

    /* Delete an application only when user own it */
    public void deleteApplication(Long id, User user) {
        Optional<JobApplication> application = getJobApplicationById(id, user);

        if (application.isPresent()) {
            jobApplicationRepository.delete(application.get());
        } else {
            throw new RuntimeException("Application not found");
        }
    }

    // Fetch application by status applied, offer , interviewed
    public List<JobApplication> getApplicationByStatus(User user, JobApplication.ApplicationStatus status) {

        return jobApplicationRepository.findByUserAndStatus(user, status);
    }

    // Fetch application between two dates (useful for reporting)
    public List<JobApplication> getApplicationBetweenDate(User user, LocalDate starDate, LocalDate endDate) {

        return jobApplicationRepository.findByUserAndAppliedDate(user, starDate, endDate);

    }

    // searching job application by companyName or position
    public List<JobApplication> searchApplication(User user, String query) {

        // find all application by companyName
        List<JobApplication> companyResults = jobApplicationRepository.findByUserCompanyName(user, query);

        // find all application by position
        List<JobApplication> positionResults = jobApplicationRepository.findByUserPosition(user, query);

        // combine both result
        companyResults.addAll(positionResults);

        // remove duplication if some application match both companyName and position

        return companyResults.stream().distinct().toList();

    }

    /*
     * Build a dashboard style statistics report for a user
     * Total application
     * count of application by status
     * count by job source (linkedin, whatsapp etc)
     * recent application in last 30 days
     * upcoming interviews
     * Pending offers
     */
    public Map<String, Object> getApplicationStats(User user) {

        // first create a hashmap to store all the details of stats
        Map<String, Object> stats = new HashMap<>();

        // get count of job application
        long totalcount = jobApplicationRepository.countApplicationByUser(user);
        stats.put("TotalApplication", totalcount);


        // status breakdown
        Map<String, Long> statusBreak = new HashMap<>();
        for (JobApplication.ApplicationStatus status : JobApplication.ApplicationStatus.values()) {
            Long count = jobApplicationRepository.countApplicationByStatus(user, status);
            statusBreak.put(status.name(), count);
        }
        stats.put("status", statusBreak);


        // source breakdown
        List<Object[]> source = jobApplicationRepository.getSourceStatsByUser(user);
        // to store the source individuals
        Map<String, Long> sourceBreak = new HashMap<>();
        for (Object[] row : source) {
            sourceBreak.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("sources", sourceBreak);


        // recent application(last 30 days)
        LocalDate thirtydaysAgo = LocalDate.now().minusDays(30);
        List<JobApplication> recentApplication = jobApplicationRepository.findByUserAndAppliedDate(user, thirtydaysAgo,
                LocalDate.now());
        stats.put("RecentApplications", recentApplication);


        // upcoming interviews
        List<JobApplication> upcomingInterview = jobApplicationRepository.findUpcomingInterviewByUser(user);
        stats.put("upcomingInterview", upcomingInterview);


        // pending offers
        List<JobApplication> pendingOffers = jobApplicationRepository.findPendingOffersByUser(user);
        stats.put("PendingOffers", pendingOffers);

        return stats;

    }

    // fetch application based on job source (like Indeed, Linkeldin)
    public List<JobApplication> getApplicationBySource(User user, JobApplication.ApplicationSource source){
        return jobApplicationRepository.findByUserAndSource(user, source);
    }

    // utility methods to count total Application
    public long getTotalApplication(User user){
      return  jobApplicationRepository.countApplicationByUser(user);
    }

    // utility method for count application by status
    public long countApplicationByStatus(User user, JobApplication.ApplicationStatus status){
        return jobApplicationRepository.countApplicationByStatus(user, status);
    }

    // Fetches most recent N application of a user 
    public List<JobApplication> getRecentApplication(User user, int limit){
      List<JobApplication> allApps  =  jobApplicationRepository.findByUserOrderedByCreatedAtDesc(user);
      return allApps.stream().limit(limit).toList();
    }

}
