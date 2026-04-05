package com.portfolio.linksaver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class NewLink {
    @NotBlank(message = "You must enter a link")
    @Pattern(regexp = "^http.*", message = "it must be a link")
    private String url;
}
