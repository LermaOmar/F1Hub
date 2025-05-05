package ptzt.f1Hub.jobs;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.application.services.lineUp.LineUpService;
import ptzt.f1Hub.application.services.market.MarketService;
import ptzt.f1Hub.domain.models.*;
import ptzt.f1Hub.domain.models.market.MarketItem;
import ptzt.f1Hub.domain.models.market.Offer;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class FinalizeAudictionScheduled {

    private final MarketService marketService;
    private final LineUpService lineUpService;
    private final AppUserService appUserService;

    @Scheduled(cron = "0 0 0 * * *")
    public void finalizeAudiction() {

        Map<League, Map<MarketItem, Offer>> map = marketService.finalizeAuction();

        map.forEach((league, marketItemOfferMap) -> {
            marketItemOfferMap.forEach((marketItem, offer) -> {

                // Get the user who placed the winning offer
                AppUser winnerUser = offer.getAppUser();
                Long winningAmount = offer.getAmount();

                // Deduct the winning amount from the user's budget
                winnerUser.setBudget(winnerUser.getBudget() - winningAmount);
                appUserService.update(winnerUser);

                // Update the LineUp to reflect the new acquisition (assuming this is how we do it)
                LineUp winningLineUp = lineUpService.getByAppUserAndLeague(winnerUser, league);

                if (marketItem.getAuctionableEntity() instanceof Driver item)

                    winningLineUp.getDrivers().add(item);
                else if (marketItem.getAuctionableEntity() instanceof Team item)

                    winningLineUp.setTeam(item);

                // Save the updated LineUp
                lineUpService.update(winningLineUp);

            });
        });

    }

}
