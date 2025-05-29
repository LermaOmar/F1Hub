package ptzt.f1Hub.application.services.verificationToken;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ptzt.f1Hub.domain.models.original.Account;
import ptzt.f1Hub.domain.models.original.VerificationToken;
import ptzt.f1Hub.exceptions.BadRequestException;
import ptzt.f1Hub.instraestructure.repository.original.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public VerificationToken creteToken(Account account) {

        VerificationToken verificationToken = VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .account(account)
                .used(false)
                .build();

        return verificationTokenRepository.save(verificationToken);

    }

    @Override
    @Transactional
    public Account verifyToken(String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Token not found"));

        if (verificationToken.isUsed())
            throw new BadRequestException("Token is used");

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Token is expired");

        verificationToken.setUsed(true);
        verificationToken.setConfirmedAt(LocalDateTime.now());
        verificationTokenRepository.save(verificationToken);

        return verificationToken.getAccount();
    }

    @Override
    public void invalidateAllTokensForUser(Account account) {

        List<VerificationToken> tokens = verificationTokenRepository.findByAccountId(account.getId());
        tokens.forEach(token -> token.setUsed(true));
        verificationTokenRepository.saveAll(tokens);

    }

    @Override
    public long deleteAllExpiredTokens() {

        return verificationTokenRepository.deleteByUsedFalseAndExpiresAtBefore(LocalDateTime.now());

    }
}