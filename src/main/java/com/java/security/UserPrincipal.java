package com.java.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;
import com.java.model.User;

/* spring security requires a userDetails object to represent 
 * an authenticated user 
 * 
 * By default spring provides a user class that implements userDetails
 * but most real world apps need custom user data like id, firstName,
 * lastName not just username and password
 * 
 * We need to create our own class userPrincipal that implements
 * userDetails and wraps around our database User entity
 * 
 * this way spring security can use it for authentication/authorization 
 */

public class UserPrincipal implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String email, String password, String firstName, String lastName,
            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.authorities = authorities;
    }

    /*
     * Create User
     * Converts your user entity into a UserPrincipal
     * Every authenticated user automatically get "ROLE_USER" authority here
     * This is where you could later expand to handle multiple roles like admin
     * 
     */
    public static UserPrincipal create(User user) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return new UserPrincipal(user.getId(),
                user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(), authorities);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;

    }

    /*
     * isAccountNonExpired(), isAccountNonLocked(),
     * isCredentialsNonExpired(), isEnabled()
     * always return true meaning account is valid and active
     * 
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /*
     * Ensures uniqueness of the logged-in user by comparing
     * only id
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserPrincipal that = (UserPrincipal) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
