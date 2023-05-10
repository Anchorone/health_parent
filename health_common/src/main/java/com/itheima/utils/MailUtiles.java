package com.itheima.utils;

import com.itheima.constant.MailConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailUtiles {

    private static JavaMailSenderImpl mailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.qq.com");
        mailSender.setPort(587);
        mailSender.setProtocol("smtp");
        mailSender.setUsername(MailConstant.MailFrom);
        mailSender.setPassword("邮箱验证码");
        mailSender.setDefaultEncoding("UTF-8");
        Properties p = new Properties();
        p.setProperty("mail.smtp.timeout", String.valueOf(35000));
        p.setProperty("mail.smtp.auth","true");
        mailSender.setJavaMailProperties(p);
        return mailSender;
    }

    public static void send(String From,String For,String Subject,String message) throws MessagingException {
        MimeMessage message1 =  mailSender().createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message1);
        helper.setText(message);
        helper.setFrom(From);
        helper.setTo(For);
        helper.setSubject(Subject);
        mailSender().send(message1);
    }
}
