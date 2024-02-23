package com.in28minutes.rest.webservices.restfulwebservices.service;

import com.in28minutes.rest.webservices.restfulwebservices.model.UserDto;

public interface UserAuthService {

    UserDto registerUser(UserDto userDto);
    UserDto updateUser(UserDto userDto);


}
