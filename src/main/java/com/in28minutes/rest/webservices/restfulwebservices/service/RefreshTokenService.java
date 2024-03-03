package com.in28minutes.rest.webservices.restfulwebservices.service;

import com.in28minutes.rest.webservices.restfulwebservices.repository.RefreshTokenRepository;
import com.in28minutes.rest.webservices.restfulwebservices.repository.UserInfoRepository;
import com.in28minutes.rest.webservices.restfulwebservices.security.jwt.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    public RefreshToken createRefreshToken(String username, Collection<? extends GrantedAuthority> authorities){

        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(userInfoRepository.findUsersByUsername(username).get())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(86400))
                .roles(authorities.stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findRefreshTokenByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " refreshToke  is expired! Please make a new Sign in");
        }
        return token;
    }



}
