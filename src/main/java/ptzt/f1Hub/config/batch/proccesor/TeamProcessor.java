package ptzt.f1Hub.config.batch.proccesor;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ptzt.f1Hub.domain.models.original.Team;
import ptzt.f1Hub.domain.models.copy.LineUp;
import ptzt.f1Hub.domain.models.copy.market.MarketItem;
import ptzt.f1Hub.instraestructure.repository.copy.TeamSecondaryRepository;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TeamProcessor implements ItemProcessor<Team, ptzt.f1Hub.domain.models.copy.Team> {

    private final TeamSecondaryRepository teamRepository;

    @Override
    public ptzt.f1Hub.domain.models.copy.Team process(Team original) {
        ptzt.f1Hub.domain.models.copy.Team copy = teamRepository.findByName(original.getName())
                .orElseGet(ptzt.f1Hub.domain.models.copy.Team::new);

        copy.setName(original.getName());
        copy.setImageUrl(original.getImageUrl());
        copy.setNationality(original.getNationality());
        copy.setPoints(original.getPoints());
        copy.setPreviousPoints(original.getPreviousPoints());
        copy.setPrice(original.getPrice());

        // Limpiar y mapear LineUps
        Set<LineUp> lineUps = new HashSet<>();


        // Limpiar y mapear MarketItems
        Set<MarketItem> marketItems = new HashSet<>();
        if (original.getMarketItems() != null) {
            original.getMarketItems().forEach(origItem -> {
                MarketItem mi = new MarketItem();
                mi.setId(null); // evitar conflictos
                mi.setAuctionableEntity(copy); // este team
                mi.setAvailable(origItem.getAvailable());
                mi.setMarkets(new HashSet<>()); // asigna si lo necesitas
                mi.setOffers(new HashSet<>()); // asigna si lo necesitas
                marketItems.add(mi);
            });
        }
        copy.setMarketItems(marketItems);

        return copy;
    }
}
