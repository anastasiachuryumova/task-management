package management.controller;

import management.dto.AuthResponseDto;
import management.dto.LoginDto;
import management.dto.RegisterDto;
import lombok.RequiredArgsConstructor;
import management.model.User;
import management.model.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import management.repository.UserRepository;
import management.repository.UserRoleRepository;
import management.security.JwtGenerator;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    private final UserRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getLogin(),
                        loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        if (userRepository.existsByLogin(registerDto.getLogin())) {
            return ResponseEntity.badRequest().body("user with this login already exists!");
        }
        User user = new User();
        user.setLastName(registerDto.getLogin());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        UserRole roles = roleRepository.findByName(registerDto.getUserRole().getName())
                .orElseThrow(() -> new RuntimeException("role not found"));
        user.setRoles(Collections.singleton(roles));
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setSurname(registerDto.getSurname());
        user.setEmail(registerDto.getEmail());
        user.setLastVisit(registerDto.getLastVisit());
        userRepository.save(user);
        return ResponseEntity.ok("User has been registered successfully!");
    }
}
