package ptzt.f1Hub.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ptzt.f1Hub.domain.models.original.AuctionableEntity;
import ptzt.f1Hub.domain.models.original.Driver;
import ptzt.f1Hub.domain.models.original.Team;
import ptzt.f1Hub.domain.models.original.market.MarketItem;
import ptzt.f1Hub.instraestructure.dto.out.auctionableEntities.AuctionableEntityOutDto;
import ptzt.f1Hub.instraestructure.dto.out.marketItems.MarketItemsOutDto;

@Mapper(componentModel = "spring")
public abstract class MarketItemMapper {

    @Autowired
    protected DriverMapper driverMapper;

    @Autowired
    protected TeamMapper teamMapper;

    @Mapping(target = "item", expression = "java(mapAuctionableEntity(marketItem.getAuctionableEntity()))")
    public abstract MarketItemsOutDto toOutDto(MarketItem marketItem);

    protected AuctionableEntityOutDto mapAuctionableEntity(AuctionableEntity entity) {
        if (entity instanceof Driver) {
            return driverMapper.toOutLimitedDto((Driver) entity);
        } else if (entity instanceof Team) {
            return teamMapper.toOutLimitedDto((Team) entity);
        }
        throw new IllegalArgumentException("Unsupported AuctionableEntity type: " + entity.getClass());
    }
}
