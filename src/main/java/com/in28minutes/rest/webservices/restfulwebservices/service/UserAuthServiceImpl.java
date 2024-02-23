package com.in28minutes.rest.webservices.restfulwebservices.service;

import com.in28minutes.rest.webservices.restfulwebservices.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserAuthServiceImpl implements UserAuthService{

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    DataSource dataSource;


    public UserDto registerUser(UserDto userDto){


        var jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        userDto.getRoles().forEach(role -> {
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_" + role));
        });
        userDto.setAuthorities(grantedAuths);

        jdbcUserDetailsManager.createUser(userDto);

        return userDto;
    }

    public UserDto updateUser(UserDto userDto) {

        var jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        UserDto updatedUser = new UserDto();
        updatedUser.setUsername(userDto.getUsername());
        updatedUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        updatedUser.setEnabled(userDto.isEnabled());

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        userDto.getRoles().forEach(role -> {
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_" + role));
        });
        updatedUser.setAuthorities(grantedAuths);

        jdbcUserDetailsManager.updateUser(updatedUser);

        return userDto;
    }


    public void deleteUser(UserDto userDto) {

        var jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.deleteUser(userDto.getUsername());
    }
}
