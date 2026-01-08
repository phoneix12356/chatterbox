package chatterbox.services;

import chatterbox.dtos.request.LoginRequestDto;
import chatterbox.dtos.request.UserRegistrationRequest;
import chatterbox.entities.Users;
import chatterbox.repository.UserRepository;
import chatterbox.utils.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class AuthServiceImpl implements AuthService {
    private final UserRepository repo;
    private final JwtUtility jwtUtility;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public Map<String, Object> loginUser(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));
        CustomUserDetail user = (CustomUserDetail) authentication.getPrincipal();
        String token = jwtUtility.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("loginToken", token);
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("avatar", user.getAvatar());

        return response;
    }

    @Override

    public void register(UserRegistrationRequest userRegistrationRequest) {
        if (repo.findByEmail(userRegistrationRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        if (repo.findByUsername(userRegistrationRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }
        Users user = this.modelMapper.map(userRegistrationRequest, Users.class);
        user.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));
        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            user.setAvatar("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png");
        }
        repo.save(user);
    }

    @Override
    public void multiRegister(List<UserRegistrationRequest> mutiSignUpDto) {
        List<Users> users = mutiSignUpDto.stream().map(userData -> {
            Users user = this.modelMapper.map(userData, Users.class);
            user.setPassword(passwordEncoder.encode(userData.getPassword()));
            return user;
        }).collect(Collectors.toList());
        repo.saveAll(users);
    }

}
