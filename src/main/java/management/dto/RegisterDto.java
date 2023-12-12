package management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import management.model.UserRole;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegisterDto {

    @NotBlank
    private String login;

    @NotBlank
    private String password;

    private UserRole userRole;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String surname;

    @NotBlank
    private String email;

    @NotBlank
    private LocalDateTime lastVisit;
}
