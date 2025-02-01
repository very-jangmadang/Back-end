package com.example.demo.service.general.impl;

import com.example.demo.base.Constants;
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
    public void sendWinnerPrizeEmail(Delivery delivery) {
        User user = delivery.getWinner();
        Raffle raffle = delivery.getRaffle();

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setFrom(fromEmail);

            String subject = "[장마당] " + raffle.getName() + " 당첨 및 배송 안내";
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariable("userName", user.getNickname());
            context.setVariable("raffleName", raffle.getName());
            context.setVariable("deliveryUrl", String.format(Constants.DELIVERY_WINNER_URL, delivery.getId()));

            context.setVariable("deliveryInfoEnd", delivery.getAddressDeadline());
            context.setVariable("fromEmail", fromEmail);

            String body = templateEngine.process("WinnerPrizeEmail.html", context);
            helper.setText(body, true);

            mailSender.send(helper.getMimeMessage());

        } catch (MessagingException e) {
            throw new CustomException(ErrorStatus.DRAW_EMAIL_FAILED);
        }
    }

    @Override
    public void sendWinnerCancelEmail(Delivery delivery) {
        User user = delivery.getWinner();
        Raffle raffle = delivery.getRaffle();

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setFrom(fromEmail);

            String subject = "[장마당] " + raffle.getName() + " 당첨 취소 안내";
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariable("userName", user.getNickname());
            context.setVariable("fromEmail", fromEmail);

            String body = templateEngine.process("WinnerCancelEmail.html", context);
            helper.setText(body, true);

            mailSender.send(helper.getMimeMessage());

        } catch (MessagingException e) {
            throw new CustomException(ErrorStatus.DRAW_EMAIL_FAILED);
        }
    }

    @Override
    public void sendOwnerAddressExpiredEmail(Delivery delivery) {
        User user = delivery.getUser();
        Raffle raffle = delivery.getRaffle();

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setFrom(fromEmail);

            String subject = "[장마당] " + raffle.getName() + " 배송지 입력 기한 만료 안내";
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariable("userName", user.getNickname());
            context.setVariable("url", String.format(Constants.DELIVERY_OWNER_URL, delivery.getId()));
            context.setVariable("fromEmail", fromEmail);

            String body = templateEngine.process("OwnerAddressExpiredEmail.html", context);
            helper.setText(body, true);

            mailSender.send(helper.getMimeMessage());

        } catch (MessagingException e) {
            throw new CustomException(ErrorStatus.DRAW_EMAIL_FAILED);
        }
    }

    @Override
    public void sendOwnerCancelEmail(Raffle raffle) {
        User user = raffle.getUser();

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setFrom(fromEmail);

            String subject = "[장마당] " + raffle.getName() + " 래플 종료 안내";
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariable("userName", user.getNickname());
            context.setVariable("raffleName", raffle.getName());
            context.setVariable("fromEmail", fromEmail);

            String body = templateEngine.process("OwnerCancelEmail.html", context);
            helper.setText(body, true);

            mailSender.send(helper.getMimeMessage());

        } catch (MessagingException e) {
            throw new CustomException(ErrorStatus.DRAW_EMAIL_FAILED);
        }
    }

    @Override
    public void sendWinnerShippingExpiredEmail(Delivery delivery) {
        User user = delivery.getWinner();
        Raffle raffle = delivery.getRaffle();

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setFrom(fromEmail);

            String subject = "[장마당] " + raffle.getName() + " 운송장 입력 기한 만료 안내";
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariable("userName", user.getNickname());
//            context.setVariable("url", String.format(Constants.DELIVERY_WINNER_URL, delivery.getId()));
            context.setVariable("fromEmail", fromEmail);

            String body = templateEngine.process("WinnerShippingExpiredEmail.html", context);
            helper.setText(body, true);

            mailSender.send(helper.getMimeMessage());

        } catch (MessagingException e) {
            throw new CustomException(ErrorStatus.DRAW_EMAIL_FAILED);
        }
    }

    @Override
    public void sendOwnerUnfulfilledEmail(Raffle raffle) {
        User user = raffle.getUser();

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setFrom(fromEmail);

            String subject = "[장마당] " + raffle.getName() + " 미추첨 안내";
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariable("userName", user.getNickname());
            context.setVariable("raffleName", raffle.getName());
            context.setVariable("url", String.format(Constants.RAFFLE_OWNER_URL, raffle.getId()));
            context.setVariable("fromEmail", fromEmail);

            String body = templateEngine.process("OwnerUnfulfilledEmail.html", context);
            helper.setText(body, true);

            mailSender.send(helper.getMimeMessage());

        } catch (MessagingException e) {
            throw new CustomException(ErrorStatus.DRAW_EMAIL_FAILED);
        }

    }

    @Override
    public void sendOwnerReadyEmail(Delivery delivery) {
        User user = delivery.getUser();
        Raffle raffle = delivery.getRaffle();

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setFrom(fromEmail);

            String subject = "[장마당] " + raffle.getName() + " 운송장 입력 안내";
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariable("userName", user.getNickname());
            context.setVariable("raffleName", raffle.getName());
            context.setVariable("deliveryUrl", String.format(Constants.DELIVERY_OWNER_URL, delivery.getId()));

            context.setVariable("deliveryInfoEnd", delivery.getShippingDeadline());
            context.setVariable("fromEmail", fromEmail);

            String body = templateEngine.process("OwnerReadyEmail.html", context);
            helper.setText(body, true);

            mailSender.send(helper.getMimeMessage());

        } catch (MessagingException e) {
            throw new CustomException(ErrorStatus.DRAW_EMAIL_FAILED);
        }
    }
}
