package chatterbox.controller;

import chatterbox.dtos.request.UserRegistrationRequest;
import chatterbox.services.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import chatterbox.dtos.request.LoginRequestDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Data
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @RequestMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto){
        System.out.println(loginRequestDto);
        try {
            String token = authService.loginUser(loginRequestDto);
            Map<String, String> response = new HashMap<>();
            response.put("loginToken", token);
            response.put("message", "Successfull login");
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    @RequestMapping("/signup")
    public ResponseEntity<?>  signUp(@RequestBody UserRegistrationRequest signUpDto){
        authService.register(signUpDto);
        return ResponseEntity.ok("Successfully Sign up");
    }

    @RequestMapping("/batch")
    public ResponseEntity<?> multipleSignUp(@RequestBody List<UserRegistrationRequest> mutiSignUpDto){
        authService.multiRegister(mutiSignUpDto);
        return ResponseEntity.ok("Sign in successfull inside mutlisignup");
    }


}
