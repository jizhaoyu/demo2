package com.example.booktrading.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 密码修改DTO
 */
@Data
public class PasswordUpdateDTO {
    
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;
    
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度6-20个字符")
    private String newPassword;
}
