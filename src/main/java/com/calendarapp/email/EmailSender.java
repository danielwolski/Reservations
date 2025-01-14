package com.calendarapp.email;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailSender {

    @Value("${email.mail}")
    private String mail;

    @Value("${email.pass}")
    private String mailPassword;

    public void sendEmail(String message, String userEmail) {
        try {
            Email email = EmailBuilder.startingBlank()
            .from(mail)
            .to(userEmail)
            .withSubject("Table reservation confirmation")
            .withPlainText(message)
            .buildEmail();

        Mailer mailer = MailerBuilder
            .withSMTPServer("smtp.gmail.com", 587, mail, mailPassword)
            .buildMailer();

        mailer.sendMail(email);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        
    }
}
