package chatterbox.services;

import chatterbox.entities.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Builder
@RequiredArgsConstructor
public class CustomUserDetail implements UserDetails {
    private final Long id;
    private final String dbUsername; // Renamed from username to avoid confusion with getUsername()
    private final String email;
    private final String password;
    private final String avatar;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getUsername() {
        return email;
    }

    public static CustomUserDetail customUserDetailFromEntity(Users user) {
        return CustomUserDetail.builder()
                .id(user.getId())
                .dbUsername(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .avatar(user.getAvatar())
                .build();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}