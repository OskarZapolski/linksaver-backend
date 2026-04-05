package com.portfolio.linksaver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogInUserData {
    @Email(message = "must be an email")
    @NotBlank
    private String email;
    @NotBlank(message = "enter a password")
    @Size(min = 6)
    private String password;
}
