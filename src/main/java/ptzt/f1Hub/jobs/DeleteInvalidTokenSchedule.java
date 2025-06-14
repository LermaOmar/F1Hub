package ptzt.f1Hub.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ptzt.f1Hub.application.services.verificationToken.VerificationTokenService;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteInvalidTokenSchedule {

    private final VerificationTokenService verificationTokenService;

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteInvalidVerificationToken(){
        verificationTokenService.deleteAllExpiredTokens();
        log.info("All invalid access token have been deleted");
    }
}
