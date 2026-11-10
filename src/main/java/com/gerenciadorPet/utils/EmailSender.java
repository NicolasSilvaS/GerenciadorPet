package com.gerenciadorPet.utils;

import com.gerenciadorPet.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EmailSender {

    private final JavaMailSender mailSender;
    private final String senderAddress;
    private final String passwordResetUrl;

    public EmailSender(
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String senderAddress,
            @Value("${app.frontend.password-reset-url}") String passwordResetUrl
    ) {
        this.mailSender = mailSender;
        this.senderAddress = senderAddress;
        this.passwordResetUrl = passwordResetUrl;
    }

    public void sendPasswordResetEmail(User user, String token) {
        String resetLink = UriComponentsBuilder.fromUriString(passwordResetUrl)
                .queryParam("token", token)
                .build()
                .encode()
                .toUriString();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderAddress);
        message.setTo(user.getEmail());
        message.setSubject("Redefinição de senha - GerenciadorPet");
        message.setText("""
                Olá, %s.

                Para redefinir sua senha, acesse o link abaixo. Ele expira em 15 minutos e só pode ser usado uma vez:
                %s

                Se você não solicitou a redefinição, ignore este e-mail.
                """.formatted(user.getUsername(), resetLink));
        mailSender.send(message);
    }
}
