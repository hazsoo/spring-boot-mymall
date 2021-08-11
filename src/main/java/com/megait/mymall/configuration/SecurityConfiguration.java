package com.megait.mymall.configuration;

import com.megait.mymall.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final MemberService memberService;

    private final DataSource dataSource;
    // DataSource : DBCP (DataBase Connection Pool)
    //  spring-data-jpa 의존성이 있다면 DataSource 빈은 자동으로 IoC 컨테이너에 등록된다.

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()

                // 다음 URL 은 인증 없이 요청 가능
                .mvcMatchers("/", "/login", "/signup", "/check-email", "/email-check-token").permitAll()

                // '/item' 으로 시작하는 자원은 get 요청만 가능
                .antMatchers(HttpMethod.GET, "/item/**").permitAll()

                // 다음 디렉토리 혹은 파일은 인증 없이 요청 가능
                 .antMatchers("/css/**", "/images/**", "/js/**", "**/favicon.ico").permitAll()
                // 이것보다는 밑에 ignoring()이 좋다.

                // 나머지 요청은 로그인 해야만 요청 가능
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .loginPage("/login")  // 안해도 기본값이 이미 '/login'임
                .defaultSuccessUrl("/", true)

                .and()
                .logout()
                .logoutUrl("/logout") // 안해도 기본값이 이미 '/logout'임
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/")

                .and()
                .rememberMe()
                    .userDetailsService(memberService)
                    .tokenRepository(tokenRepository());

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        // commontLocations으로 등록되어있는 모든 정적 리소스
    }

    @Bean
    public PersistentTokenRepository tokenRepository(){
        // PersistentTokenRepository : rememberMe 기능에 사용되는 토큰값들을 관리하는 Repository
        JdbcTokenRepositoryImpl jdbcTokenRepository =
                new JdbcTokenRepositoryImpl();

        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
}