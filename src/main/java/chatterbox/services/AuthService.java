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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repo;
    private final JwtUtility jwtUtility;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    public String loginUser(LoginRequestDto loginRequestDto) {
        System.out.println("insider login service + " + loginRequestDto);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));
        System.out.println("inside login service " + authentication.getPrincipal());
        CustomUserDetail user = (CustomUserDetail) authentication.getPrincipal();
        return jwtUtility.generateToken(user);
    }

    public void register(UserRegistrationRequest userRegistrationRequest) {
        Users user = this.modelMapper.map(userRegistrationRequest, Users.class);
        user.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));
        repo.save(user);
    }

    public void multiRegister(List<UserRegistrationRequest> mutiSignUpDto) {
        List<Users> users = mutiSignUpDto.stream().map(userData -> {
            Users user = this.modelMapper.map(userData, Users.class);
            user.setPassword(passwordEncoder.encode(userData.getPassword()));
            return user;
        }).collect(Collectors.toList());
        repo.saveAll(users);
    }

}
