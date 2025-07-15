package com.example.demo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotBlank(message = "Требуется ввести имя")
    @Size(min = 3, max = 50, message = "Имя должно быть от 3 до 5 символов")
    private String username;

    @NotBlank(message = "Требуется ввести email")
    @Email(message = "Неправильный формат email")
    private String email;

    @NotBlank(message = "Требуется ввести пароль")
    @Size(min = 6, message = "Пароль должен состоять не менее чем из 6 символов")
    private String password;

    @NotBlank(message = "Требуется ввести повторный пароль")
    private String confirmPassword;

    private String phoneNumber;

    @NotBlank
    private String userType;
}