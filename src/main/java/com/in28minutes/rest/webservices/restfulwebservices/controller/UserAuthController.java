package com.in28minutes.rest.webservices.restfulwebservices.controller;

import com.in28minutes.rest.webservices.restfulwebservices.model.UserDto;
import com.in28minutes.rest.webservices.restfulwebservices.service.UserAuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserAuthController {

    @Autowired
    UserAuthServiceImpl userAuthService;


    @PostMapping("/registerUser")
    public ResponseEntity<String> doRegister(@RequestBody UserDto userDto) {

        try {
            userAuthService.registerUser(userDto);

            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Unable to save User", e);
        }
    }
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @PostMapping("/updateUser")
    public ResponseEntity<String> doUpdate(@RequestBody UserDto userDto) {

        try {
            userAuthService.updateUser(userDto);

            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Unable to update User", e);
        }
    }

    //@PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/deleteUser")
    public ResponseEntity<String> doDelete(@RequestBody UserDto userDto) {

        try {
            userAuthService.deleteUser(userDto);

            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Unable to delete User", e);
        }
    }
}
