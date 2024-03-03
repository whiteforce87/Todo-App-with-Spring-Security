package com.in28minutes.rest.webservices.restfulwebservices.security.jwt;

import com.in28minutes.rest.webservices.restfulwebservices.model.RefreshTokenRequest;
import com.in28minutes.rest.webservices.restfulwebservices.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@RestController
public class JwtAuthenticationController {
    
    private final JwtTokenService tokenService;
    
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.accessTokenCookieName}")
    private String cookieName;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Value("${jwt.cookieExpiry}")
    long cookieExpiry;

    public JwtAuthenticationController(JwtTokenService tokenService, 
            AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JwtResponse> generateToken(
            @RequestBody JwtTokenRequest jwtTokenRequest, HttpServletResponse response) {
        
        var authenticationToken = 
                new UsernamePasswordAuthenticationToken(
                        jwtTokenRequest.username(), 
                        jwtTokenRequest.password());
        
        var authentication = 
                authenticationManager.authenticate(authenticationToken);

        var token = tokenService.generateToken(authentication);


        //This 2 line code is for implementing refreshToken
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(jwtTokenRequest.username(), authentication.getAuthorities());
        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken.getToken())
                .build();

/*
        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(cookieExpiry)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());


*/
        CookieUtil.create(response, cookieName, token, true, 86400, "localhost");

        return ResponseEntity.ok(jwtResponse);


    }
/*
    @PostMapping("/authenticate")
    public ResponseEntity<JwtTokenResponse> generateToken(
            @RequestBody JwtTokenRequest jwtTokenRequest) {

        var authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        jwtTokenRequest.username(),
                        jwtTokenRequest.password());

        var authentication =
                authenticationManager.authenticate(authenticationToken);

        var token = tokenService.generateToken(authentication);


        return ResponseEntity.ok(new JwtTokenResponse(token));
    }
 */

    @PostMapping("/refreshToken")
    public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){


        Optional<RefreshToken> optRefreshToken = refreshTokenService.findByToken(refreshTokenRequest.getRefreshToken());
        RefreshToken refreshToken = optRefreshToken.get();
        List<String> roles = Arrays.stream(refreshToken.getRoles().split(",")).toList();

        return optRefreshToken
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = tokenService.prepareTokenForRefreshToken(userInfo.getUsername(),roles);
                    return JwtResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenRequest.getRefreshToken())
                            .build();
                }).orElseThrow(() -> new RuntimeException( "RefreshToken is not in Database!"));


    }

    @GetMapping(path = "/checkAuth")
    public ResponseEntity<String> checkAuthCheck(HttpServletRequest request, HttpServletResponse response) {


        Cookie[] cookies = request.getCookies();
        String cookieValue = null;


        if (cookies != null) {
            // Iterate through cookies to find the desired cookie
            for (Cookie cookie : cookies) {
                    // Found the desired HTTP-only cookie
                     cookieValue = cookie.getValue();

            }
        }


        String cookieString = String.format("%s=%s; Secure; HttpOnly; SameSite=Strict", cookies[0].getName(), cookies[0].getValue());
        response.addHeader("Set-Cookie", cookieString);
        //response.addHeader("Set-Cookie",cookieValue);


        return ResponseEntity.status(HttpStatus.OK).body("Successful Check");

    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }


        CookieUtil.clear(response, cookieName);

        return ResponseEntity.status(HttpStatus.OK).body("Logout successful");
    }
}


