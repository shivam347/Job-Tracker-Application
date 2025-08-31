package com.java.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/* this class is used to send details of token, email and other details when the user logged in or authenticated successfully
 * to the client for further api calls
 */

@Data
@AllArgsConstructor
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String email;
    private String firstName;
    private String lastName;
    private boolean gmailConnected;


    
}
