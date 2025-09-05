package com.java.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

/* Requires configuration to connect your springboot application to the gmail api 
 * since gmail api requires authentication (Oauth2), JSON PARSING , a Secure HTTP transport channel 
 * 
 */

@Configuration
public class GmailConfig {

  /*
   * read application name from the application.properties
   * this name is send to gmail while making api call for gmail access
   * to know which app is making call
   */

  private String applicationName;

  /*
   * we also need json parser as it gmail api uses json for requests and responses
   */
  private static final JsonFactory json_factory = GsonFactory.getDefaultInstance();

  /*
   * define permission that app requests from gmail , app can only read emails and
   * nothing more it can do
   */
  private static final List<String> permissions = Collections.singletonList(GmailScopes.GMAIL_READONLY);

  /*
   * path to your google cloud oauth client credentials file
   * this is like passport for gmail login without this you will not login
   * in the gmail account this holds some information
   */
  private static final String CREDENTIALS_FILE = "/credentials.json";

  /*
   * secure http transport layer for all Gmail APi calls
   * handles ssl/ tls security under the hood
   * marked @bean spring will manage this object and inject
   * it wherever needed
   */
  @Bean
  public NetHttpTransport httpTransport() throws GeneralSecurityException, IOException {
    return GoogleNetHttpTransport.newTrustedTransport();
  }

  /*
   * Provides a JSON factory (parser/serializer) bean
   * Required by the Gmail Api client to handle JSON responses
   */
  @Bean
  public JsonFactory jsonFactory() {
    return json_factory;
  }

  /*
   * creates a main method for getting a gmail Client
   * creates a secure http transport connection like tls or ssl
   * wrap the given access token into a googleCredentials
   * Build a gmail object
   * return to service class to call gmail api methods
   */
  public Gmail getGmailService(String accessToken) throws IOException, GeneralSecurityException {
    NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(accessToken, null))
        .createScoped(permissions);

        //convert credentials into request initializer 
        // needed something that can automatically attach credentials to Http Requests
        // httpcredentialsAdapter is a wrapper that adapts google credentials 

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

    return new Gmail.Builder(httpTransport, json_factory, requestInitializer)
        .setApplicationName(applicationName)
        .build();
  }


  /*getClientSecrets loads Oauth2 client credentials from credentials.json */
  private GoogleCredentials getCredentials()throws IOException{
    InputStream in = GmailConfig.class.getResourceAsStream(CREDENTIALS_FILE);

    if(in == null){
      throw new FileNotFoundException("Resources not found: "+ CREDENTIALS_FILE);

    }

    return GoogleCredentials.fromStream(in).createScoped(permissions);
  }

}
