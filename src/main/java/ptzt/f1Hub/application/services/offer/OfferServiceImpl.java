package ptzt.f1Hub.application.services.offer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.application.services.market.item.MarketItemService;
import ptzt.f1Hub.exceptions.EntityNotFoundException;
import ptzt.f1Hub.exceptions.UnproccesableEntityException;
import ptzt.f1Hub.domain.models.AppUser;
import ptzt.f1Hub.domain.models.Budget;
import ptzt.f1Hub.domain.models.LineUp;
import ptzt.f1Hub.domain.models.market.MarketItem;
import ptzt.f1Hub.domain.models.market.Offer;
import ptzt.f1Hub.instraestructure.repository.OfferRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService{

    private final OfferRepository offerRepository;
    private final AppUserService appUserService;
    private final MarketItemService marketItemService;


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

        Optional<Offer> opOffer = offerRepository
                .findByAppUserAndLeagueAndMarketItem(offer.getAppUser(),offer.getLeague(),offer.getMarketItem());

        if (opOffer.isPresent() && opOffer.get().getAppUser().getId().equals(offer.getAppUser().getId()))
            throw new UnproccesableEntityException("This user already post a offer for this item");


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

    private void validateOffer(Offer offer) {

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

        //Already have a team
        if (lineUp.getTeam() != null) {
            throw new UnproccesableEntityException("You already have a team");
        }

        //Already have 2 drivers
        if (lineUp.getDrivers().size() >= 2) {
            throw new UnproccesableEntityException("You already have 2 drivers");
        }
    }

}
