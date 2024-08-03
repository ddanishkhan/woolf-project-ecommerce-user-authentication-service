package com.ecommerce_user_authentication.service;

import com.ecommerce_user_authentication.dto.UserDto;
import com.ecommerce_user_authentication.model.SessionEntity;
import com.ecommerce_user_authentication.model.SessionStatus;
import com.ecommerce_user_authentication.model.UserEntity;
import com.ecommerce_user_authentication.repository.SessionRepository;
import com.ecommerce_user_authentication.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;


    public AuthenticationService(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public ResponseEntity<UserDto> login(String email, String password) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return null;
        }
        UserEntity user = userOptional.get();
        if (!user.getPassword().equals(password)) {
            return null;
        }
        String token = RandomStringUtils.randomAlphanumeric(30);
        SessionEntity session = new SessionEntity();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);
        UserDto userDto = new UserDto(email, user.getRoleEntities());
        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:" + token);
        ResponseEntity<UserDto> response = new ResponseEntity<>(userDto, headers, HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.SET_COOKIE, token);
        return response;
    }

    public void logout(String token, String email) {
        Optional<SessionEntity> sessionOptional = sessionRepository.findByTokenAndEmail(token, email);
        if (sessionOptional.isEmpty()) {
            return;
        }
        SessionEntity session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.ENDED);
        sessionRepository.save(session);
    }

    public UserDto signUp(String email, String password) {
        var user = new UserEntity();
        user.setEmail(email);
        user.setPassword(password);
        UserEntity savedUser = userRepository.save(user);
        return UserDto.from(savedUser);
    }

    public SessionStatus validate(String token, String email) {
        Optional<SessionEntity> sessionOptional = sessionRepository.findByTokenAndEmail(token, email);
        if (sessionOptional.isEmpty()) {
            return null;
        }
        return SessionStatus.INVALID;
    }
}
