package com.ecommerce_user_authentication.service;

import com.ecommerce_user_authentication.dto.UserDto;
import com.ecommerce_user_authentication.model.SessionEntity;
import com.ecommerce_user_authentication.model.SessionStatus;
import com.ecommerce_user_authentication.model.UserEntity;
import com.ecommerce_user_authentication.repository.SessionRepository;
import com.ecommerce_user_authentication.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;


    public AuthenticationService(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public ResponseEntity<UserDto> login(String email, String password) {
        Optional<UserEntity> userOptional = userRepository.findOneByEmail(email);
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
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.SET_COOKIE, "auth-token=\"" + token + "\"");
        return new ResponseEntity<>(userDto, responseHeaders, HttpStatus.OK);
    }

    public void logout(String token, String email) {
        Optional<SessionEntity> sessionOptional = sessionRepository.findByTokenAndUser_Email(token, email);
        if (sessionOptional.isEmpty()) {
            log.info("No session found.");
            return;
        }
        SessionEntity session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.ENDED);
        sessionRepository.save(session);
    }

    public Optional<UserDto> signUp(String email, String password) {
        var user = new UserEntity();
        user.setEmail(email);
        user.setPassword(password);
        if (userRepository.findOneByEmail(email).isEmpty()) {
            UserEntity savedUser = userRepository.save(user);
            return Optional.of(UserDto.from(savedUser));
        }
        return Optional.empty();
    }

    public SessionStatus validate(String token, String email) {
        Optional<SessionEntity> sessionOptional = sessionRepository.findByTokenAndUser_Email(token, email);
        if (sessionOptional.isEmpty()) {
            return SessionStatus.INVALID;
        }
        return sessionOptional.get().getSessionStatus();
    }
}
