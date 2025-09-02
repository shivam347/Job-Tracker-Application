package com.java.security;

import java.security.Key;
import java.util.Date;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

/* Utility class for handling jwt (Json web token ) 
 * 
 * 
 * @component -> Makes this class as spring bean
 * so that it can injected(autowired) into other classes
 * 
 * Logger -> Used for logging jwt errors
 * 
*/

@Component
public class JwtUtils {

   private static final Logger logger =  LoggerFactory.getLogger(JwtUtils.class);

    /* jwtSecret -> secret key for 
     * signing JWTS(Should be long and Base 64-encoded)
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirations;

    /*Secret key method
     * converts the base64-encoded secret(jwt.secret) into a key object
     * used for signing and validating tokens
     */
    private Key key(){
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
    }

    /* used for logged-in user (authentication)
     * create jwt with
     * subject - email
     * issued at - current time
     * expiration time - current time + expiration time
     * signs with HS256 algorithm and secret key
     * returns the jwt string 
     */

    public String generateJwtToken(Authentication authentication){

      UserPrincipal userPrincipal =  (UserPrincipal) authentication.getPrincipal();

      return Jwts.builder()
      .setSubject(userPrincipal.getEmail())
      .setIssuedAt(new Date())
      .setExpiration(new Date(new Date().getTime() + jwtExpirations))
      .signWith(key(), SignatureAlgorithm.HS256)
      .compact();
        
    }

    /* Generate token from email
     * used when you want to reset password
     * don't want to password to enter 
     * Simply using email
     */

    public String generateTokenFromEmail(String email){

        return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(new Date().getTime() + jwtExpirations))
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
    }

    /*USED in authentication filters 
     * to get the logged in user identity from the token
     * parses the token 
     * returns the subject (the user's email) 
     * 
     * when you logged in this is used to know 
     * which user is logged in so that spring
     * can load user from the db and continue processing the request
     */
    public String extractEmailFromToken(String token){

        return Jwts.parserBuilder() // parser is used to validate and decode JWT tokens
        .setSigningKey(key())  // sets the secret key for verifying the jwt signature
        .build() // build the configured jwt parser
        .parseClaimsJws(token) // validate the token 
        .getBody()  // retrieves claims payload
        .getSubject(); // returns the value of subject field from claims
    }


    /* Method to validate the token */
    public boolean validateToken(String authToken){

        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;

        } catch (MalformedJwtException e) {

            logger.error("Invalid jwt token: {}", e.getMessage());
            
        }catch (ExpiredJwtException e) {

            logger.error("Expired jwt token: {}", e.getMessage());
            
        }
        catch (UnsupportedJwtException e) {

            logger.error("Invalid jwt token: {}", e.getMessage());
            
        }catch(IllegalArgumentException e){
            
            logger.error("Token string field is empty: {}", e.getMessage());
        }

        return false;
    }


    
}
