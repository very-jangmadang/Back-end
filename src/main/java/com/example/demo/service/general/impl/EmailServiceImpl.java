package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.service.general.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;


@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmail(Delivery delivery) {
        User user = delivery.getWinner();
        Raffle raffle = delivery.getRaffle();

        try {

            if (user.getEmail() == null)
                throw new CustomException(ErrorStatus.DRAW_NO_WINNER_EMAIL);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setFrom(fromEmail);

            String subject = "[장마당] " + raffle.getName() + " 당첨 및 배송 안내";
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariable("userName", user.getNickname());
            context.setVariable("raffleName", raffle.getName());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            context.setVariable("deliveryInfoEnd", delivery.getAddressDeadline());
            context.setVariable("fromEmail", fromEmail);

            String body = templateEngine.process("EmailTemplate.html", context);
            helper.setText(body, true);

            mailSender.send(helper.getMimeMessage());

        } catch (MessagingException e) {
            throw new CustomException(ErrorStatus.DRAW_EMAIL_FAILED);
        }
    }
}
