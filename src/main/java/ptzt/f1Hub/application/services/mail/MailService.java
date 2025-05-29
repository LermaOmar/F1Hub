package ptzt.f1Hub.application.services.mail;

import ptzt.f1Hub.domain.models.Account;

public interface MailService {

    void sendVerificationEmail(Account account, String token);

}
