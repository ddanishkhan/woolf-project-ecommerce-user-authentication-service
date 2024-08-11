package com.ecommerce_user_authentication.service;

import com.ecommerce_user_authentication.dto.UserDto;
import com.ecommerce_user_authentication.exception.InvalidLoginCredentialsException;
import com.ecommerce_user_authentication.exception.InvalidRoleException;
import com.ecommerce_user_authentication.exception.InvalidTokenException;
import com.ecommerce_user_authentication.exception.UserAlreadyExistsException;
import com.ecommerce_user_authentication.model.RoleEnum;
import com.ecommerce_user_authentication.model.SessionEntity;
import com.ecommerce_user_authentication.model.SessionStatus;
import com.ecommerce_user_authentication.model.UserEntity;
import com.ecommerce_user_authentication.repository.RoleRepository;
import com.ecommerce_user_authentication.repository.SessionRepository;
import com.ecommerce_user_authentication.repository.UserRepository;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, SessionRepository sessionRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public UserEntity authenticate(String email, String password){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        return userRepository.findOneByEmail(email).orElseThrow();
    }

    public ResponseEntity<UserDto> login(String email, String inputPassword) {
        Optional<UserEntity> userOptional = userRepository.findOneByEmail(email);
        if (userOptional.isEmpty()) {
            throw new InvalidLoginCredentialsException("User does not exist.");
        }
        UserEntity userEntity = authenticate(email, inputPassword);

        if (!passwordEncoder.matches(inputPassword, userEntity.getPassword())) {
            throw new InvalidLoginCredentialsException("Invalid password.");
        }

        String token = jwtService.generateToken(userEntity);
        SessionEntity session = new SessionEntity();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(userEntity);
        sessionRepository.save(session);

        UserDto userDto = new UserDto(email, userEntity.getRoleEntity());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setBearerAuth(token);
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
        var savedUser = signUpV2(email, password);
        return Optional.of(UserDto.from(savedUser));
    }

    public UserEntity signUpV2(String email, String password) {
        if (userRepository.findOneByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered.");
        }
        var roleEntity = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(()-> new InvalidRoleException("Role does not exist."));

        var user = new UserEntity();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoleEntity(roleEntity);
        return userRepository.save(user);
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
                .verifyWith(jwtService.getSignInKey())
                .build();
        try {
            jwtParser.parse(token);
        } catch (Exception e) {
            log.error("error validating jwt", e);
            throw new InvalidTokenException("Could not verify JWT token integrity!");
        }
    }

}
