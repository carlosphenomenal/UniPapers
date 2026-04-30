package com.unipapers.backend.Modules.Auth.Services;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class EmailService {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String contentString) {
        Email from = new Email(fromEmail);
        Email recipient = new Email(to);
        Content content = new Content("text/plain", contentString);
        Mail mail = new Mail(from, subject, recipient, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            log.info("SendGrid response status code: {}", response.getStatusCode());
            if (response.getStatusCode() >= 400) {
                log.error("Error sending email via SendGrid: {}", response.getBody());
            }
        } catch (IOException ex) {
            log.error("Failed to send email to {}", to, ex);
        }
    }

    public void sendVerificationCode(String to, String code) {
        String subject = "Your UniPapers Verification Code";
        String content = "Welcome to UniPapers! Your verification code is: " + code;
        sendEmail(to, subject, content);
    }
}
