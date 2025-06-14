package ptzt.f1Hub.application.services.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ptzt.f1Hub.domain.models.original.Account;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    private final static String ACTIVATION_EMAIL_TEMPLATE = "verification_email.html";

    @Override
    public void sendVerificationEmail(Account account, String token) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom("efeuno.hub@gmail.com");
            helper.setTo(account.getEmail());
            helper.setSubject("Activate your account");
            helper.setText(processVerificationTemplate(account.getUsername(), token), true);

            mailSender.send(mimeMessage);

        } catch (MessagingException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private String processVerificationTemplate(String user, String token) {
        Map<String, Object> model = new HashMap<>();
        model.put("username", user);
        model.put("activationUrl", "https://lermaomar.github.io/F1Hub-Front/#/activate/" + token);

        Context context = new Context();
        context.setVariables(model);

        return templateEngine.process(ACTIVATION_EMAIL_TEMPLATE, context);
    }

}
