
package ptzt.f1Hub.instraestructure.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ptzt.f1Hub.application.services.league.LeagueService;
import ptzt.f1Hub.application.services.market.MarketService;
import ptzt.f1Hub.application.services.market.item.MarketItemService;
import ptzt.f1Hub.domain.mappers.LeagueMapper;
import ptzt.f1Hub.domain.mappers.MarketItemMapper;
import ptzt.f1Hub.domain.models.AppUser;
import ptzt.f1Hub.domain.models.AuctionableEntity;
import ptzt.f1Hub.domain.models.League;
import ptzt.f1Hub.domain.models.LineUp;
import ptzt.f1Hub.domain.models.market.Market;
import ptzt.f1Hub.domain.models.market.MarketItem;
import ptzt.f1Hub.exceptions.UserUnauthorizedException;
import ptzt.f1Hub.instraestructure.dto.in.league.LeagueInIdDto;
import ptzt.f1Hub.instraestructure.dto.out.marketItems.MarketItemsOutDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.DefaultResponseDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.PageOutDto;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/marketItems")
@RequiredArgsConstructor
public class MarketItemController {

    private final MarketItemMapper  marketItemMapper;

    private final MarketItemService marketItemService;
    private final MarketService marketService;
    private final LeagueService leagueService;

    @GetMapping
    public ResponseEntity<PageOutDto<MarketItemsOutDto>> getAll(@PageableDefault Pageable pageable){

        return ResponseEntity.ok(
                new PageOutDto<>(marketItemService.getAll(pageable).map(marketItemMapper::toOutDto))
        );

    }

    @PutMapping("/{id}/display")
    public ResponseEntity<DefaultResponseDto> displayInMarket(@PathVariable Long id,
                                                              @Valid @RequestBody LeagueInIdDto leagueInIdDto,
                                                              Authentication authentication){

        MarketItem marketItem = marketItemService.getById(id);

        League league = leagueInIdDtoToEntity(leagueInIdDto);
        checkSameUser(league, marketItem,authentication);

        List<Market> markets = new ArrayList<>();
        markets.add(marketService.getByLeague(league));

        marketItemService.displayInMarket(marketItem,markets);

        return ResponseEntity.ok(
                new DefaultResponseDto(200, "Item has been displayed in the market")
        );

    }


    @PutMapping("/{id}/hide")
    public ResponseEntity<DefaultResponseDto> hideInMarket(@PathVariable Long id,
                                                           @Valid @RequestBody LeagueInIdDto leagueInIdDto,
                                                           Authentication authentication){

        MarketItem marketItem = marketItemService.getById(id);

        League league = leagueInIdDtoToEntity(leagueInIdDto);

        checkSameUser(league, marketItem,authentication);

        List<Market> markets = new ArrayList<>();
        markets.add(marketService.getByLeague(league));
        marketItemService.hideInMarket(marketItemService.getById(id),markets);

        return ResponseEntity.ok(
                new DefaultResponseDto(200, "Item has been hiden in the market")
        );

    }

    private void checkSameUser(League league, MarketItem marketItem, Authentication authentication) {
        LineUp lineUp = league.getLineUps().stream()
                .filter(lineUp1->
                        lineUp1.getDrivers().stream()
                                .map(AuctionableEntity::getId)
                                .toList()
                                .contains(marketItem.getAuctionableEntity().getId())
                ).toList().get(0);

        AppUser appUser = lineUp.getAppUser();

        if (!appUser.getAccount().getEmail().equals(authentication.getName()))
            throw new UserUnauthorizedException("You're unauthorized to manipulate other's lineup");
    }

    private League leagueInIdDtoToEntity(LeagueInIdDto leagueInIdDto){

        return leagueService.getById(leagueInIdDto.getId());

    }

}
