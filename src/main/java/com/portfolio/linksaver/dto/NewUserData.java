package com.portfolio.linksaver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewUserData {
    @Email(message = "enter email")
    @NotBlank
    private String email;
    @NotBlank(message = "enter password")
    @Size(min = 6)
    private String password;
    @NotBlank(message = "enter userName")
    private String userName;
}
