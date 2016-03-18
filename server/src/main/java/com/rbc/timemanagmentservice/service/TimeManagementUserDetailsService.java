package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Roles;
import com.rbc.timemanagmentservice.model.User;
import com.rbc.timemanagmentservice.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by russbaker on 2/22/16.
 */
@Service(value = "userDetailsService")
public final class TimeManagementUserDetailsService implements UserDetailsService{
    private final UserRepository userRepository;

    @Autowired
    public TimeManagementUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private MessageSource messages;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return new org.springframework.security.core.userdetails.User(
                    user.get().getUsername(),  user.get().getPassword(), true, true, true, true,
                    getAuthorities(user.get().getRoles()));
        }
        throw new UsernameNotFoundException("Could not find user with username: " + username);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(List<Roles> roles) {
        return getGrantedAuthorities(roles);
    }



    private List<SimpleGrantedAuthority> getGrantedAuthorities(List<Roles> roles) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roles.stream().forEach(role ->authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRole().name().toUpperCase())));
        return authorities;
    }
}
