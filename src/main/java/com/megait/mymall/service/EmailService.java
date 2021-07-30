package com.megait.mymall.service;

import com.megait.mymall.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailService {

    @Autowired
    MailSender consoleMailSender;

    @Transactional
    public void sendEmail(Member member) {

        member.generateEmailCheckToken();

        String url = "http://127.0.0.1:8080/email-check-token?token="
                + member.getEmailCheckToken() + "&email=" + member.getEmail();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(member.getEmail());
        message.setFrom("admin@mymall.com");
        message.setSubject("[mymall] 회원가입 이메일 인증 링크입니다.");
        message.setText("다음 링크를 클릭해주세요. =>" + url);
        consoleMailSender.send(message);
    }
}
