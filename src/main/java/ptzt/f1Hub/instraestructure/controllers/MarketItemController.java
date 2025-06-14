
package ptzt.f1Hub.instraestructure.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ptzt.f1Hub.application.services.account.AccountService;
import ptzt.f1Hub.application.services.appUser.AppUserService;
import ptzt.f1Hub.application.services.league.LeagueService;
import ptzt.f1Hub.application.services.market.MarketService;
import ptzt.f1Hub.application.services.market.item.MarketItemService;
import ptzt.f1Hub.application.services.offer.OfferService;
import ptzt.f1Hub.domain.mappers.MarketItemMapper;
import ptzt.f1Hub.domain.models.original.*;
import ptzt.f1Hub.domain.models.original.market.Market;
import ptzt.f1Hub.domain.models.original.market.MarketItem;
import ptzt.f1Hub.exceptions.EntityNotFoundException;
import ptzt.f1Hub.exceptions.UserUnauthorizedException;
import ptzt.f1Hub.instraestructure.dto.in.league.LeagueInIdDto;
import ptzt.f1Hub.instraestructure.dto.out.marketItems.MarketItemsOutDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.DefaultResponseDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.PageOutDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/marketItems")
@RequiredArgsConstructor
public class MarketItemController {

    private final MarketItemMapper  marketItemMapper;

    private final OfferService offerService;
    private final AppUserService appUserService;
    private final AccountService accountService;
    private final MarketItemService marketItemService;
    private final MarketService marketService;
    private final LeagueService leagueService;

    @GetMapping
    public ResponseEntity<PageOutDto<MarketItemsOutDto>> getAll(@PageableDefault Pageable pageable){

        return ResponseEntity.ok(
                new PageOutDto<>(marketItemService.getAll(pageable).map(marketItemMapper::toOutDto))
        );

    }

    @GetMapping("/{id}/league")
    public ResponseEntity<PageOutDto<MarketItemsOutDto>> getAllByMarket(@PageableDefault Pageable pageable, @PathVariable Long id){

        return ResponseEntity.ok(
                new PageOutDto<>(
                        marketItemService.getAllByMarket(
                            marketService.getByLeague(leagueService.getById(id)),pageable
                        ).map(marketItemMapper::toOutDto)
                )
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

    private void checkSameUser(League league,
                               MarketItem marketItem,
                               Authentication authentication) {

        AuctionableEntity entity = marketItem.getAuctionableEntity();

        LineUp lineUp = findLineUpContaining(league, entity)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No lineup contiene ese ítem en la liga " + league.getId()
                ));

        String ownerEmail = lineUp.getAppUser().getAccount().getEmail();
        if (! ownerEmail.equals(authentication.getName())) {
            throw new UserUnauthorizedException(
                    "No puedes manipular el mercado de otro usuario"
            );
        }
    }

    private Optional<LineUp> findLineUpContaining(League league, AuctionableEntity entity) {
        return league.getLineUps().stream()
                .filter(lu -> {
                    if (entity instanceof Driver driver) {
                        return lu.getDrivers().stream()
                                .anyMatch(d -> d.getId().equals(driver.getId()));
                    } else if (entity instanceof Team team) {
                        return lu.getTeam() != null
                                && lu.getTeam().getId().equals(team.getId());
                    } else {
                        return false;
                    }
                })
                .findFirst();
    }


    private League leagueInIdDtoToEntity(LeagueInIdDto leagueInIdDto){

        return leagueService.getById(leagueInIdDto.getId());

    }

}
