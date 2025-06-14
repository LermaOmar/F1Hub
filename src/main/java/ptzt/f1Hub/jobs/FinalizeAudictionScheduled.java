package ptzt.f1Hub.jobs;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ptzt.f1Hub.application.services.account.AccountService;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.application.services.lineUp.LineUpService;
import ptzt.f1Hub.application.services.market.MarketService;
import ptzt.f1Hub.application.services.budget.BudgetService;
import ptzt.f1Hub.domain.models.original.*;
import ptzt.f1Hub.domain.models.original.market.MarketItem;
import ptzt.f1Hub.domain.models.original.market.Offer;
import ptzt.f1Hub.instraestructure.repository.original.MarketItemRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinalizeAudictionScheduled {

    private final MarketService marketService;
    private final LineUpService lineUpService;
    private final AppUserService appUserService;
    private final AccountService accountService;
    private final BudgetService budgetService;
    private final MarketItemRepository marketItemRepository;


    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void finalizeAuction() {

        //EFEUNO USER
        AppUser systemUser = appUserService.getByAccount(
                accountService.getByEmail("efeuno.hub@gmail.com")
        );

        // MAP <LEAGUE , MAP<MARKET ITEM, BEST OFFER>>
        Map<League, Map<MarketItem, Offer>> results = marketService.finalizeAuction();

        results.forEach((league, itemOfferMap) -> {

            // ALL LINEUPS BY LEAGUE
            List<LineUp> leagueLineUps = lineUpService
                    .getAllByLeague(Pageable.unpaged(), league)
                    .getContent();

            itemOfferMap.forEach((marketItem, winningOffer) -> {
                AuctionableEntity entity = marketItem.getAuctionableEntity();
                AppUser buyer = winningOffer.getAppUser();
                long amount = winningOffer.getAmount();

                //GET SELLER
                Optional<LineUp> sellerLineUpOpt = leagueLineUps.stream()
                        .filter(lu -> isEntityInLineUp(entity, lu))
                        .findFirst();

                //PROCESS SELLER
                sellerLineUpOpt.ifPresent(sellerLu -> {
                    AppUser seller = sellerLu.getAppUser();
                    boolean isSystemSeller = seller.getAccount().getEmail()
                            .equalsIgnoreCase(systemUser.getAccount().getEmail());

                    //REMOVE ITEM FROM SELLER LINEUP
                    if (entity instanceof Driver) {
                        sellerLu.getDrivers().remove((Driver) entity);
                    } else {
                        sellerLu.setTeam(null);
                    }
                    lineUpService.update(sellerLu);

                    if (!isSystemSeller) {

                        //WHEN ITS NOT EFEUNO USER

                        //INCREASE THE SELLER BUDGET WITH OFFER VALUE
                        Budget sellerBudget = seller.getBudgets().stream()
                                .filter(b -> b.getLeague().equals(league))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException(
                                        "No budget for seller " + seller.getId()
                                ));
                        sellerBudget.setBudgetValue(
                                sellerBudget.getBudgetValue() + amount
                        );
                        budgetService.update(sellerBudget);
                    }
                });

                //PROCESS BUYER
                boolean isSystemBuyer = buyer.getAccount().getEmail()
                        .equalsIgnoreCase(systemUser.getAccount().getEmail());
                if (!isSystemBuyer) {

                    //WHEN ITS NOT EFEUNO USER

                    //DECREASE THE BUYER BUDGET WITH OFFER VALUE
                    Budget buyerBudget = buyer.getBudgets().stream()
                            .filter(b -> b.getLeague().equals(league))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException(
                                    "No budget for buyer " + buyer.getId()
                            ));
                    buyerBudget.setBudgetValue(
                            buyerBudget.getBudgetValue() - amount
                    );
                    budgetService.update(buyerBudget);

                    //ADD ITEM TO LINEUP
                    LineUp buyerLu = lineUpService.getByAppUserAndLeague(buyer, league);
                    if (entity instanceof Driver) {
                        buyerLu.getDrivers().add((Driver) entity);
                    } else {
                        buyerLu.setTeam((Team) entity);
                    }
                    lineUpService.update(buyerLu);
                }

                //ITEM NOT AVAILABLE SO DON'T APPEAR MORE AT MARKET
                marketItem.setAvailable(false);
                marketItem.getMarkets().clear();
                marketItemRepository.save(marketItem);

                log.info(
                        "League={} | Item={} sell by={} bought by={} for {}",
                        league.getName(),
                        entity.getId(),
                        sellerLineUpOpt.isPresent() ? sellerLineUpOpt.get().getId() : "EFEUNO.HUB",
                        buyer.getAccount().getEmail().equals("efeuno.hub@gmail.com") ? "EFEUNO.HUB" : buyer.getId(),
                        amount
                );
            });
        });

        log.info("===============================");
        log.info("ALL AUCTIONS HAVE BEEN FINISHED");
        log.info("===============================");

    }


    private boolean isEntityInLineUp(AuctionableEntity entity, LineUp lu) {
        if (entity instanceof Team) {
            return lu.getTeam() != null
                    && lu.getTeam().getId().equals(entity.getId());
        } else {
            return lu.getDrivers().stream()
                    .map(AuctionableEntity::getId)
                    .anyMatch(id -> id.equals(entity.getId()));
        }
    }


}
