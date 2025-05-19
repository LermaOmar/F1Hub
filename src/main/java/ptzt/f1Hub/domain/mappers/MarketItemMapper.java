package ptzt.f1Hub.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ptzt.f1Hub.domain.models.market.MarketItem;
import ptzt.f1Hub.instraestructure.dto.out.marketItems.MarketItemsOutDto;

@Mapper(componentModel = "spring")
public interface MarketItemMapper {

    @Mapping(target = "item", source = "auctionableEntity")
    MarketItemsOutDto toOutDto(MarketItem marketItem);

}
