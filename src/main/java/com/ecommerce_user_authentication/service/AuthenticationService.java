package com.ecommerce_user_authentication.service;

import com.ecommerce_user_authentication.dto.UserDto;
import com.ecommerce_user_authentication.exception.InvalidLoginCredentialsException;
import com.ecommerce_user_authentication.exception.UserAlreadyExistsException;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public AuthenticationService(UserRepository userRepository, SessionRepository sessionRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<UserDto> login(String email, String inputPassword) {
        Optional<UserEntity> userOptional = userRepository.findOneByEmail(email);
        if (userOptional.isEmpty()) {
            throw new InvalidLoginCredentialsException("User does not exist.");
        }
        UserEntity userEntity = userOptional.get();

        if (!passwordEncoder.matches(inputPassword, userEntity.getPassword())) {
            throw new InvalidLoginCredentialsException("Invalid password.");
        }
        String token = RandomStringUtils.randomAlphanumeric(30);
        SessionEntity session = new SessionEntity();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(userEntity);
        sessionRepository.save(session);

        UserDto userDto = new UserDto(email, userEntity.getRoleEntities());
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
        user.setPassword(passwordEncoder.encode(password));
        if (userRepository.findOneByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered.");
        }
        UserEntity savedUser = userRepository.save(user);
        return Optional.of(UserDto.from(savedUser));
    }

    public SessionStatus validate(String token, String email) {
        Optional<SessionEntity> sessionOptional = sessionRepository.findByTokenAndUser_Email(token, email);
        if (sessionOptional.isEmpty()) {
            return SessionStatus.INVALID;
        }
        return sessionOptional.get().getSessionStatus();
    }
}
