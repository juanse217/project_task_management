package com.sebastian.dev.projecttaskmanagement.shared;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public record RegisterRequest(
    @NotBlank(message = "The username is required for Registration")
    @Size(min = 5, max = 255, message = "The username must be between 5-255 characters")
    String username,
    @NotBlank(message = "The password is required for Registration")
    @Pattern(regexp = "^[\\x20-\\x7E]{8,64}$", message = "Invalid password") //NIST pattern. Allows all printable ascii characters. Focuses on length rather than complexity.
    String password
) {

}
