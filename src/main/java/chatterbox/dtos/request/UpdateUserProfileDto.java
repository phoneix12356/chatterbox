package chatterbox.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserProfileDto {

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    @NotBlank(message = "Avatar cannot be empty")
    private String avatar;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;
}
