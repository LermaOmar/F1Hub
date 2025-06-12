package ptzt.f1Hub.instraestructure.dto.out.marketItems;

import ptzt.f1Hub.domain.models.copy.AuctionableEntity;
import ptzt.f1Hub.instraestructure.dto.out.auctionableEntities.AuctionableEntityOutDto;
import ptzt.f1Hub.instraestructure.dto.out.offer.OfferOutDto;

import java.util.List;

public record MarketItemsOutDto(

        Long id,

        AuctionableEntityOutDto item,

        List<OfferOutDto> offers,

        boolean available

) {
}
