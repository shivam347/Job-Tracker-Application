package com.java.dto;

import lombok.Data;

/* this class is used to send details of token, email and other details when the user logged in or authenticated successfully
 * to the client for further api calls
 */

@Data

public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String email;
    private String firstName;
    private String lastName;
    private boolean gmailConnected;


    public JwtResponse(String token, String email, String firstName, String lastName,
            boolean gmailConnected) {
        this.token = token;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gmailConnected = gmailConnected;
    }

    


    
}
