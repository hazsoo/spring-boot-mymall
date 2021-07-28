package com.megait.mymall.configuration;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity // 이거 안해주면 기본꺼로 설정됨
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()

                // 다음 URL 은 인증 없이 요청 가능
                .mvcMatchers("/", "/login", "/signup", "/check-email", "/email-check-token").permitAll()

                // '/item' 으로 시작하는 자원은 get 요청만 가능
                .antMatchers(HttpMethod.GET, "/item/*").permitAll()

                // 다음 디렉토리 혹은 파일은 인증 없이 요청 가능
                // .antMatchers("/css/**", "/images/**", "/js/**", "**/favicon.ico").permitAll()
                // 이것보다는 밑에 ignoring()이 좋다.

                // 나머지 요청은 로그인 해야만 요청 가능
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .loginPage("/login")  // 안해도 기본값이 이미 '/login'임
                .defaultSuccessUrl("/", true);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        // commontLocations으로 등록되어있는 모든 정적 리소스
    }
}