package ptzt.f1Hub.instraestructure.dto.out.marketItems;

import ptzt.f1Hub.instraestructure.dto.out.offer.OfferOutDto;

import java.util.List;

public record MarketItemsOutDto(

        Long id,

        Object item,

        List<OfferOutDto> offers,

        boolean available

) {
}
