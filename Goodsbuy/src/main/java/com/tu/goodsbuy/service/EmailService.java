package com.tu.goodsbuy.service;

import com.tu.goodsbuy.repository.ProfileRepository;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;


@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {


    private static final SecureRandom RANDOM = new SecureRandom();

    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender emailSender;
    private final ProfileRepository profileRepository;

    private MimeMessage createMessage(String to, String ePw) throws Exception {

        log.info("보내는 대상 : " + to);

        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);//보내는 대상
        message.setSubject("[Goodsbuy] 회원가입정보 인증 메일입니다.");//제목

        String msgg = "";
        msgg += "<div style='margin:20px;'>";
        msgg += "<h1> Goodsbuy 이메일 인증 번호 </h1>";
        msgg += "<br>";
        msgg += "<p>아래 코드를 복사해 입력해주세요<p>";
        msgg += "<br>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += ePw + "</strong><div><br/> ";
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress(from, "GoodsBuy"));//보내는 사람

        return message;
    }

    public String createKey() {
        byte[] bytes = new byte[4];
        RANDOM.nextBytes(bytes);
        return java.util.HexFormat.of().formatHex(bytes);
    }

    public String sendMessage(String to) throws Exception {
        String ePw = createKey();
        MimeMessage message = createMessage(to, ePw);
        try {//예외처리
            emailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return ePw;
    }

    public boolean isDuplicatedEmail(String email) {
        return profileRepository.isDuplicatedEmail(email);
    }
}
