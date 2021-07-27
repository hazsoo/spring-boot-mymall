package com.megait.mymall.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;

@Slf4j
@Component
@Profile("local")
public class ConsoleMailSender implements JavaMailSender {
    // JavaMailSender 란? spring-boot-start-mail 에 들어있는 메일 전송 인터페이스

    @Override
    public MimeMessage createMimeMessage() { // MIME 형식으로 메일보낼때, 해당 메시지를 생성
        return null;
    }

    @Override
    public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
        return null;
    }

    @Override
    public void send(MimeMessage mimeMessage) throws MailException {

    }

    @Override
    public void send(MimeMessage... mimeMessages) throws MailException {

    }

    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {

    }

    @Override
    public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {

    }

    @Override // 메일전송 실행하는 메서드
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        log.info("To. {}", simpleMessage.getTo());
        log.info("From. {}", simpleMessage.getFrom());
        log.info("Title. {}", simpleMessage.getSubject());
        log.info("Content. {}", simpleMessage.getText());
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {

    }
}
