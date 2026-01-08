package chatterbox.controller;

import chatterbox.dtos.request.UserRegistrationRequest;
import chatterbox.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import chatterbox.dtos.request.LoginRequestDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        try {
            Map<String, Object> response = authService.loginUser(loginRequestDto);
            response.put("message", "Successfull login");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserRegistrationRequest signUpDto) {
        authService.register(signUpDto);
        return ResponseEntity.ok("Successfully Sign up");
    }

    @RequestMapping("/batch")
    public ResponseEntity<?> multipleSignUp(@Valid @RequestBody List<UserRegistrationRequest> mutiSignUpDto) {
        authService.multiRegister(mutiSignUpDto);
        return ResponseEntity.ok("Sign in successfull inside mutlisignup");
    }

}
