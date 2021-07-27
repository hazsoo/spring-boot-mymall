package com.megait.mymall.validation;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data  // @Getter, @Setter 두 개 쓴거랑 동일
public class SignUpForm {

    @NotBlank(message = "필수 항목입니다.")
    @Length(min = 5, max = 40, message = "이메일은 5자 이상 40자 이하여야합니다.")
    @Email(message = "이메일 형식을 지켜주세요. (예. test@test.com)")
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[#?!@$%^&*-]).{8,}$",
            message = "패스워드는 영문자, 숫자, 특수기호를 조합하여 최소 8자 이상을 입력하셔야 합니다."
    )
    private String password;

    @NotBlank(message = "반드시 약관에 동의하셔야합니다.")
    private String agreeTermsOfService;

    private String street;
    private String city;

    @Pattern(regexp = "(^[0-9]+$|^$)")
    private String zipcode;

    @AssertTrue(message = "올바른 주소 형식을 지켜주세요.")
    public boolean isValidAddress() {
        return (street == null && city == null && zipcode == null) ||
                (street.isBlank() && city.isBlank() && zipcode.isBlank()) ||
                (!street.isBlank() && !city.isBlank() && !zipcode.isBlank());
    }
}
