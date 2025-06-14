package ptzt.f1Hub.application.services.verificationToken;

import ptzt.f1Hub.domain.models.original.Account;
import ptzt.f1Hub.domain.models.original.VerificationToken;

public interface VerificationTokenService {

    VerificationToken creteToken(Account account);

    Account verifyToken(String verifcationToken);

    void invalidateAllTokensForUser(Account account);

    void deleteAllExpiredTokens();

}
