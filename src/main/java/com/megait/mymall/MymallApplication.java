package com.megait.mymall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MymallApplication {

	public static void main(String[] args) {
		SpringApplication.run(MymallApplication.class, args);
	}

	@Bean // 어디다가 둬도 상관 없음 다만 component 스캔 되는 곳에 넣어야함
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

}
