package ptzt.f1Hub.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.application.services.league.LeagueService;
import ptzt.f1Hub.application.services.market.item.MarketItemService;
import ptzt.f1Hub.domain.models.market.Offer;
import ptzt.f1Hub.instraestructure.dto.in.offer.OfferInDto;
import ptzt.f1Hub.instraestructure.dto.in.offer.OfferInUpdateDto;
import ptzt.f1Hub.instraestructure.dto.out.offer.OfferOutDto;

@Mapper(componentModel = "spring", uses = {AppUserService.class, MarketItemService.class, LeagueService.class})
public interface OfferMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "appUser", source = "appUser")
    @Mapping(target = "marketItem", source = "marketItem")
    @Mapping(target = "league", source = "league")
    Offer toEntity(OfferInDto offerInDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "appUser", ignore = true)
    @Mapping(target = "marketItem", ignore = true)
    @Mapping(target = "league", ignore = true)
    void toUpdate(OfferInUpdateDto offerInUpdateDto, @MappingTarget Offer offer);

    OfferOutDto toOutDto(Offer offer);

}
