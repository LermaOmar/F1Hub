package ptzt.f1Hub.application.services.offer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.application.services.market.item.MarketItemService;
import ptzt.f1Hub.application.services.team.TeamService;
import ptzt.f1Hub.domain.models.original.*;
import ptzt.f1Hub.exceptions.EntityNotFoundException;
import ptzt.f1Hub.exceptions.UnproccesableEntityException;
import ptzt.f1Hub.domain.models.original.market.MarketItem;
import ptzt.f1Hub.domain.models.original.market.Offer;
import ptzt.f1Hub.instraestructure.repository.original.OfferRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService{

    private final OfferRepository offerRepository;
    private final AppUserService appUserService;
    private final MarketItemService marketItemService;
    private final TeamService teamService;


    @Override
    public List<Offer> getAll() {

        return offerRepository.findAll();

    }

    @Override
    public Page<Offer> getAllByUser(Pageable pageable, AppUser appUser) {

        return offerRepository.findAllByAppUser(pageable, appUser);

    }

    @Override
    public Offer getById(Long id) {

        return offerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no Offer with that ID"));

    }

    @Transactional
    @Override
    public Offer create(Offer offer) {
        AppUser user = offer.getAppUser();
        League league = offer.getLeague();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<Offer> todayOffers = offerRepository
                .findAllByAppUserAndLeagueAndCreatedAtBetween(user, league, startOfDay, endOfDay);

        if (user.getAccount().getEmail().equalsIgnoreCase("efeuno.hub@gmail.com")) {
            return offerRepository.save(offer);
        }

        LineUp userLineUp = user.getLineUps().stream()
                .filter(lu -> lu.getLeague().getId().equals(league.getId()))
                .findFirst()
                .orElseThrow(() -> new UnproccesableEntityException("You are not registered in this league."));

        boolean isTeamOffer = offer.getMarketItem().getAuctionableEntity() instanceof Team;
        boolean isDriverOffer = offer.getMarketItem().getAuctionableEntity() instanceof Driver;

        if (isTeamOffer) {
            if (userLineUp.getTeam() != null) {
                throw new UnproccesableEntityException("You already have a team.");
            }

            long teamOffers = todayOffers.stream()
                    .filter(o -> o.getMarketItem().getAuctionableEntity() instanceof Team)
                    .count();

            if (teamOffers >= 1) {
                throw new UnproccesableEntityException("You have already made an offer for a team.");
            }
        }

        if (isDriverOffer) {
            int driversMissing = 2 - userLineUp.getDrivers().size();

            if (driversMissing <= 0) {
                throw new UnproccesableEntityException("You already have 2 drivers.");
            }

            long driverOffers = todayOffers.stream()
                    .filter(o -> o.getMarketItem().getAuctionableEntity() instanceof Driver)
                    .count();

            if (driverOffers >= driversMissing) {
                throw new UnproccesableEntityException("You have already made all possible driver offers.");
            }
        }

        validateOffer(offer);
        return offerRepository.save(offer);
    }


    @Override
    public Offer update(Offer offer) {

        validateOffer(offer);
        return offerRepository.save(offer);

    }

    @Transactional
    @Override
    public void delete(Offer offer) {

        AppUser appuser = offer.getAppUser();
        appuser.getOffers().removeIf(offer1 -> offer1.getId().equals(offer.getId()));
        appUserService.update(appuser);
        
        MarketItem marketItem = offer.getMarketItem();
        marketItem.getOffers().removeIf(offer1 -> offer1.getId().equals(offer.getId()));
        marketItemService.update(marketItem);

        offerRepository.delete(offer);

    }

    @Override
    public Optional<Offer> getOfferByMarketItemAndAppUserAndLeague(AppUser appUser, League league, MarketItem marketItem) {

        return offerRepository.findByAppUserAndLeagueAndMarketItem(appUser,league,marketItem);

    }

    @Override
    public Page<Offer> getOffersByMarketItemrAndLeague(Pageable pageable, League league, MarketItem marketItem) {

        return offerRepository.findByLeagueAndMarketItem(league,marketItem, pageable);

    }

    private void validateOffer(Offer offer) {

        if (offer.getAppUser().getAccount().getEmail().equalsIgnoreCase("efeuno.hub@gmail.com"))
            return;

        if (!offer.getMarketItem().getAvailable())
            throw new UnproccesableEntityException("Item is not available");

        Budget budget = offer.getAppUser().getBudgets().stream()
                .filter(budget1 -> budget1.getAppUser().getId().equals(offer.getAppUser().getId())
                        && budget1.getLeague().getId().equals(offer.getLeague().getId()))
                .findFirst()
                .orElseThrow(() -> new UnproccesableEntityException("Budget not found for user and league"));


        LineUp lineUp = offer.getAppUser().getLineUps().stream()
                .filter(lineUp1 -> lineUp1.getAppUser().getId().equals(offer.getAppUser().getId())
                        && lineUp1.getLeague().getId().equals(offer.getLeague().getId()))
                .findFirst()
                .orElseThrow(() -> new UnproccesableEntityException("LineUp not found for user and league"));

        //Budget lower than offer
        if (budget.getBudgetValue() < offer.getAmount()) {
            throw new UnproccesableEntityException("The offer is higher than your budget");
        }

        //Offer is 0
        if (offer.getAmount() <= 0)
            throw new UnproccesableEntityException("Amount not valid ");
    }

}
