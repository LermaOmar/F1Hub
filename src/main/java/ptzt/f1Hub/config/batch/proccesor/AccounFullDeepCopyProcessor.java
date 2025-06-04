package ptzt.f1Hub.config.batch.proccesor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import ptzt.f1Hub.domain.models.original.Account;
import ptzt.f1Hub.domain.models.original.AppUser;
import ptzt.f1Hub.domain.models.original.AuctionableEntity;
import ptzt.f1Hub.domain.models.original.Budget;
import ptzt.f1Hub.domain.models.original.League;
import ptzt.f1Hub.domain.models.original.LineUp;
import ptzt.f1Hub.domain.models.original.VerificationToken;
import ptzt.f1Hub.domain.models.original.market.MarketItem;
import ptzt.f1Hub.domain.models.original.market.Offer;

import ptzt.f1Hub.domain.models.copy.*;
import ptzt.f1Hub.domain.models.copy.market.*;
import ptzt.f1Hub.instraestructure.repository.copy.AccountSecondaryRepository;
import ptzt.f1Hub.instraestructure.repository.original.MarketItemRepository;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccounFullDeepCopyProcessor implements ItemProcessor<Account, ptzt.f1Hub.domain.models.copy.Account>, StepExecutionListener {

    private final MarketItemRepository marketItemRepository;
    private final AccountSecondaryRepository accountSecondaryRepository;
    private StepExecution stepExecution;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Account processing completed");
        return ExitStatus.COMPLETED;
    }

    @Override
    public ptzt.f1Hub.domain.models.copy.Account process(Account original) {
        log.info("Processing account with id: {}", original.getId());
        ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();

        if (!executionContext.containsKey("account"))
            executionContext.putInt("account", 0);

        ptzt.f1Hub.domain.models.copy.Account copy = accountSecondaryRepository.findByUsername(original.getUsername())
                .orElse(new ptzt.f1Hub.domain.models.copy.Account());

        copy.setActive(original.isActive());
        copy.setRoles(new HashSet<>(original.getRoles()));
        copy.setEmail(original.getEmail());
        copy.setUsername(original.getUsername());
        copy.setPassword(original.getPassword());

        if (original.getAppUser() != null) {
            ptzt.f1Hub.domain.models.copy.AppUser userCopy = copy.getAppUser();
            if (userCopy == null) {
                userCopy = new ptzt.f1Hub.domain.models.copy.AppUser();
                userCopy.setAccount(copy);
                userCopy.setBudgets(new HashSet<>());
                userCopy.setOffers(new HashSet<>());
                userCopy.setLineUps(new HashSet<>());
                copy.setAppUser(userCopy);
            } else {
                userCopy.getBudgets().clear();
                userCopy.getOffers().clear();
                userCopy.getLineUps().clear();
            }

            for (Budget b : original.getAppUser().getBudgets()) {
                ptzt.f1Hub.domain.models.copy.Budget bc = new ptzt.f1Hub.domain.models.copy.Budget();
                bc.setBudgetValue(b.getBudgetValue());
                bc.setLeague(copyLeague(b.getLeague()));
                bc.setAppUser(userCopy);
                userCopy.getBudgets().add(bc);
            }

            for (Offer o : original.getAppUser().getOffers()) {
                ptzt.f1Hub.domain.models.copy.market.Offer oc = new ptzt.f1Hub.domain.models.copy.market.Offer();
                oc.setAmount(o.getAmount());
                oc.setCreatedAt(o.getCreatedAt());
                oc.setLeague(copyLeague(o.getLeague()));
                oc.setAppUser(userCopy);
                userCopy.getOffers().add(oc);
            }

            for (LineUp lu : original.getAppUser().getLineUps()) {
                ptzt.f1Hub.domain.models.copy.LineUp luc = new ptzt.f1Hub.domain.models.copy.LineUp();
                luc.setAppUser(userCopy);
                luc.setLeague(copyLeague(lu.getLeague()));
                luc.setTeam((ptzt.f1Hub.domain.models.copy.Team) copyAuctionable(lu.getTeam()));

                Set<ptzt.f1Hub.domain.models.copy.Driver> drivers = new HashSet<>();
                lu.getDrivers().forEach(driver -> drivers.add((ptzt.f1Hub.domain.models.copy.Driver) copyAuctionable(driver)));
                luc.setDrivers(drivers);

                userCopy.getLineUps().add(luc);
            }
        }

        if (copy.getVerificationTokens() == null) {
            copy.setVerificationTokens(new HashSet<>());
        } else {
            copy.getVerificationTokens().clear();
        }

        for (VerificationToken vt : original.getVerificationTokens()) {
            ptzt.f1Hub.domain.models.copy.VerificationToken vtc = new ptzt.f1Hub.domain.models.copy.VerificationToken();
            vtc.setToken(vt.getToken());
            vtc.setConfirmedAt(vt.getConfirmedAt());
            vtc.setExpiresAt(vt.getExpiresAt());
            vtc.setAccount(copy);
            copy.getVerificationTokens().add(vtc);
        }

        int account = executionContext.getInt("account");
        executionContext.putInt("account", ++account);

        return copy;
    }

    private ptzt.f1Hub.domain.models.copy.League copyLeague(League original) {
        if (original == null) return null;

        ptzt.f1Hub.domain.models.copy.League l = new ptzt.f1Hub.domain.models.copy.League();
        l.setName(original.getName());

        ptzt.f1Hub.domain.models.copy.market.Market m = new ptzt.f1Hub.domain.models.copy.market.Market();
        m.setLeague(l);
        l.setMarket(m);

        List<MarketItem> linkedItems = marketItemRepository.findAllByMarkets(List.of(original.getMarket()));

        for (MarketItem item : linkedItems) {
            ptzt.f1Hub.domain.models.copy.market.MarketItem copyItem = new ptzt.f1Hub.domain.models.copy.market.MarketItem();
            copyItem.setAuctionableEntity(copyAuctionable(item.getAuctionableEntity()));
            copyItem.setAvailable(item.getAvailable());
            copyItem.getMarkets().add(m);

            Set<ptzt.f1Hub.domain.models.copy.market.Offer> offerCopies = new HashSet<>();
            for (Offer offer : item.getOffers()) {
                ptzt.f1Hub.domain.models.copy.market.Offer offerCopy = new ptzt.f1Hub.domain.models.copy.market.Offer();
                offerCopy.setAmount(offer.getAmount());
                offerCopy.setCreatedAt(offer.getCreatedAt());
                offerCopy.setMarketItem(copyItem);
                offerCopy.setAppUser(null);
                offerCopy.setLeague(l);
                offerCopies.add(offerCopy);
            }
            copyItem.setOffers(offerCopies);
        }


        return l;
    }

    private ptzt.f1Hub.domain.models.copy.AuctionableEntity copyAuctionable(AuctionableEntity original) {
        if (original instanceof ptzt.f1Hub.domain.models.original.Team team) {
            ptzt.f1Hub.domain.models.copy.Team copy = new ptzt.f1Hub.domain.models.copy.Team();
            copy.setName(team.getName());
            copy.setImageUrl(team.getImageUrl());
            copy.setNationality(team.getNationality());
            copy.setPoints(team.getPoints());
            copy.setPreviousPoints(team.getPreviousPoints());
            copy.setPrice(team.getPrice());
            return copy;
        } else if (original instanceof ptzt.f1Hub.domain.models.original.Driver driver) {
            ptzt.f1Hub.domain.models.copy.Driver copy = new ptzt.f1Hub.domain.models.copy.Driver();
            copy.setName(driver.getName());
            copy.setImageUrl(driver.getImageUrl());
            copy.setNationality(driver.getNationality());
            copy.setPoints(driver.getPoints());
            copy.setPreviousPoints(driver.getPreviousPoints());
            copy.setPrice(driver.getPrice());
            return copy;
        }
        return null;
    }
}
