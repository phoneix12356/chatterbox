package chatterbox.services;

import chatterbox.dtos.request.LoginRequestDto;
import chatterbox.dtos.request.UserRegistrationRequest;

import java.util.List;
import java.util.Map;


public interface AuthService {
    Map<String, Object> loginUser(LoginRequestDto loginRequestDto);
    void register(UserRegistrationRequest userRegistrationRequest);

    void multiRegister(List<UserRegistrationRequest> mutiSignUpDto);
}
