package com.java.service;





import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.java.dto.JwtResponse;
import com.java.dto.LoginRequest;
import com.java.dto.RegisterRequest;
import com.java.model.User;
import com.java.repository.UserRepository;
import com.java.security.JwtUtils;
import com.java.security.UserPrincipal;

/* Authentication -> verify user credentials and generate JWT tokens
 * Registration -> create new user account with password encryption
 * 
 * User Management -> Get current user, manage Gmail Connections
 * Security Integration -> Work with spring security components
 * 
 */

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    /*Inject spring security authentication manager which is used 
     * to authenticate users by checking credentials like email
     * and password
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /*Inject user repository interface that connects 
     * to the database and provides methods like 
     * findByEmail, existsByEmail and save
     */
    @Autowired
    private UserRepository userRepository;


    /*Injects a password encoder Like BCryptPasswordEncoder used 
     * to hash passwords before saving and compare during login
     */
    @Autowired
    private PasswordEncoder encoder;

    /* Injects a custom JwtUtils utility class 
     * that generate and validates JWT tokens
     */
    @Autowired 
    private JwtUtils jwtUtils;

    /* authenticateUser handles login
     * validates credentials using authentication manager
     * if valid sets authentication in the security context holder so
     * the user is logged in
     * generate a jwt token for session less authentication
     * loads full user details (emails, firstName, LastName, Gmail connection
     * returns a jwtResponse contains (token + user info )
     * 
     * without this users can't log in securely
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest){
      Authentication authentication =  authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

          String jwt  =  jwtUtils.generateJwtToken(authentication);
          
          UserPrincipal userPrincipal = (UserPrincipal)authentication.getPrincipal();

        //   get user details
        User user = userRepository.findByEmail(userPrincipal.getEmail())
        .orElseThrow(() -> new RuntimeException("user not found"));

        return new JwtResponse(jwt,
        userPrincipal.getEmail(),
        userPrincipal.getFirstName(), userPrincipal.getLastName(),
         user.getGmailConnected());



    }

    /*registerUser handles registration
     * checks if email already exists
     * if not creates a new user object
     * Encrypts the password using passwordEncoder 
     * saves the user in the database via User Repository
     */
    public User registerUser(RegisterRequest signUp){

        // checks email already exists or not 
        if(userRepository.existsByEmail(signUp.getEmail())){
            throw new RuntimeException("Error! email already exists");
        }

        User user = new User(signUp.getEmail(), encoder.encode(signUp.getPassword()),
        signUp.getFirstName(), signUp.getLastName());

        return userRepository.save(user);

    }

    /* getCurrent user useful when we want to know who is making request like saving user jobs
     * reads authentication info from securityContextHolder
     * extracts User Principal 
     * find User in DB using email
     * 
     */
    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof UserPrincipal){
            UserPrincipal userPrincipal = (UserPrincipal)authentication.getPrincipal();
            return userRepository.findByEmail(userPrincipal.getEmail())
            .orElseThrow(() -> new RuntimeException("user not found"));
        }

        throw new RuntimeException("No authenticate user found");
    }

    /*update gmailConnection updates user gmail integration details
     * Stores gmail access token + refresh token in the user entity
     * marks gmailConnected = true
     * saves updated user in db
     * logs the update 
     * app will link gmail so users can use gmail-related features
     */
    public void updateGmailConnection(User user, String token, String refreshToken){
        user.setGmailToken(token);
        user.setGmailRefreshToken(refreshToken);
        user.setGmailConnected(true);
        userRepository.save(user);

        logger.info("Gmail connection update for the user: {}", user.getEmail());
    }

    /*disconnectGmail - giving users control to unlink gmail 
     * whenever possible they want
     * clears gmail tokens
     * marks gmailconnected = false;
     * saves changes in db 
     * logs the connection
     */
    public void disconnectGmail(User user){
        user.setGmailToken(null);
        user.setGmailRefreshToken(null);
        user.setGmailConnected(false);
        userRepository.save(user);

        logger.info("Gmail disconnected for the user : {}", user.getEmail());
    }




    
}
