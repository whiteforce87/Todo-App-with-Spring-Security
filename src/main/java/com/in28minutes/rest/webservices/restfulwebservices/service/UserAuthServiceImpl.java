package com.in28minutes.rest.webservices.restfulwebservices.service;

import com.in28minutes.rest.webservices.restfulwebservices.model.UserDto;
import com.in28minutes.rest.webservices.restfulwebservices.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    UserInfoService userInfoService;





    @Transactional
    public UserDto registerUser(UserDto userDto){

        saveUser(userDto);

        var jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        userDto.getRoles().forEach(role -> {
            grantedAuths.add(new SimpleGrantedAuthority(role));
        });
        userDto.setAuthorities(grantedAuths);

        jdbcUserDetailsManager.createUser(userDto);

        return userDto;
    }

    @Transactional
    public UserDto updateUser(UserDto userDto) {

        saveUser(userDto);

        var jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        UserDto updatedUser = new UserDto();
        updatedUser.setUsername(userDto.getUsername());
        updatedUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        updatedUser.setEnabled(userDto.isEnabled());

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        userDto.getRoles().forEach(role -> {
            grantedAuths.add(new SimpleGrantedAuthority(role));
        });
        updatedUser.setAuthorities(grantedAuths);

        jdbcUserDetailsManager.updateUser(updatedUser);

        return userDto;
    }

    @Transactional
    public void deleteUser(UserDto userDto) {

        UserInfo userToDelete = userInfoService.findUserByName(userDto.getUsername()).get();

        userInfoService.delete(userToDelete);

        var jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.deleteUser(userDto.getUsername());
    }

    private UserInfo saveUser(UserDto userDto){

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(userDto.getUsername());
        userInfo.setAddress(userDto.getAddress());
        userInfo.setEmail(userDto.getEmail());
        userInfo.setGender(userDto.getGender());
        userInfo.setPhone(userDto.getPhone());

        return userInfoService.createUser(userInfo);
    }
}
