package ptzt.f1Hub.application.services.verificationToken;

import ptzt.f1Hub.domain.models.Account;
import ptzt.f1Hub.domain.models.AppUser;
import ptzt.f1Hub.domain.models.VerificationToken;

public interface VerificationTokenService {

    VerificationToken creteToken(Account account);

    Account verifyToken(String verifcationToken);

    void invalidateAllTokensForUser(Account account);

    long deleteAllExpiredTokens();

}
