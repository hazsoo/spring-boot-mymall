package com.megait.mymall.validation;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data // = @Getter + @Setter
public class SignUpForm {

    @NotBlank
    @Length(min = 5, max = 40)
    @Email
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[#?!@$%^&*-]).{8,}$",
            message = "패스워드는 영문자, 숫자, 특수기호를 조합하여 최소 8자 이상을 입력하셔야 합니다."
    )
    private String password;

    @NotBlank
    private String agreeTermsOfService; // 약관동의

    private String street;
    private String city;

    @Pattern(regexp = "^[0-9]{5}$") // TODO 정규식 맞는지 확인할 것
    private String zipcode;
}
