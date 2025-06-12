package ptzt.f1Hub.jobs;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ptzt.f1Hub.application.services.verificationToken.VerificationTokenService;

@Component
@RequiredArgsConstructor
public class DeleteInvalidTokenSchedule {

    private final VerificationTokenService verificationTokenService;

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteInvalidVerificationToken(){
        verificationTokenService.deleteAllExpiredTokens();
    }
}
