package com.ecommerce_user_authentication.service;

import com.ecommerce_user_authentication.dto.UserDto;
import com.ecommerce_user_authentication.exception.InvalidLoginCredentialsException;
import com.ecommerce_user_authentication.exception.InvalidTokenException;
import com.ecommerce_user_authentication.exception.UserAlreadyExistsException;
import com.ecommerce_user_authentication.model.SessionEntity;
import com.ecommerce_user_authentication.model.SessionStatus;
import com.ecommerce_user_authentication.model.UserEntity;
import com.ecommerce_user_authentication.repository.SessionRepository;
import com.ecommerce_user_authentication.repository.UserRepository;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecretKey key;
    private final MacAlgorithm alg = Jwts.SIG.HS256; // HS256 algo added for JWT

    public AuthenticationService(UserRepository userRepository, SessionRepository sessionRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
        key = alg.key().build(); // generating the secret key
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
        String token = generateJwt(userEntity);
        SessionEntity session = new SessionEntity();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(userEntity);
        sessionRepository.save(session);

        UserDto userDto = new UserDto(email, userEntity.getRoleEntities());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setBearerAuth(token);
        responseHeaders.set(HttpHeaders.SET_COOKIE, "auth-token=\"" + token + "\"");
        return new ResponseEntity<>(userDto, responseHeaders, HttpStatus.OK);
    }

    private String generateJwt(UserEntity user) {
        //start adding the claims
        Map<String, Object> jsonForJWT = new HashMap<>();
        jsonForJWT.put("email", user.getEmail());
        jsonForJWT.put("roles", user.getRoleEntities());
        jsonForJWT.put("createdAt", new Date());
        jsonForJWT.put("expiryAt", Date.from(ZonedDateTime.now().plusDays(1).toInstant()));

        return Jwts.builder()
                .claims(jsonForJWT) // added the claims
                .signWith(key, alg) // added the algo and key
                .compact(); //building the token
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
        validateJwt(token);
        Date expiryAt = getExpiryFromJwt(token);
        Optional<SessionEntity> sessionOptional = sessionRepository.findByTokenAndUser_Email(token, email);
        if (sessionOptional.isEmpty()
                || sessionOptional.get().getExpiringAt().after(new Date())
                || expiryAt.after(new Date())) {
            return SessionStatus.INVALID;
        }

        return sessionOptional.get().getSessionStatus();
    }

    private Date getExpiryFromJwt(String token) {
        String[] chunks = token.split("\\.");
        JsonParser p = new JacksonJsonParser();
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        Map<String, Object> claims = p.parseMap(payload);
        return new Date((Long) claims.getOrDefault("expiryAt", 0));
    }

    void validateJwt(String token) {
        JwtParser jwtParser = Jwts.parser()
                .verifyWith(key)
                .build();
        try {
            jwtParser.parse(token);
        } catch (Exception e) {
            log.error("error validating jwt", e);
            throw new InvalidTokenException("Could not verify JWT token integrity!");
        }
    }

}
