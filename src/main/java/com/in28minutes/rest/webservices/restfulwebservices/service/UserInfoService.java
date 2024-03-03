package com.in28minutes.rest.webservices.restfulwebservices.service;

import com.in28minutes.rest.webservices.restfulwebservices.model.UserInfo;
import com.in28minutes.rest.webservices.restfulwebservices.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    public UserInfo createUser(UserInfo userInfo){
        return userInfoRepository.save(userInfo);
    }

    public Optional<UserInfo> findUserByName(String username) {
        return userInfoRepository.findUsersByUsername(username);

    }

    public void delete(UserInfo userToDelete) {
        userInfoRepository.delete(userToDelete);
    }
}
