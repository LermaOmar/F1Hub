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
import ptzt.f1Hub.application.services.market.item.MarketItemService;
import ptzt.f1Hub.application.services.offer.OfferService;
import ptzt.f1Hub.domain.mappers.OfferMapper;
import ptzt.f1Hub.domain.models.original.market.Offer;
import ptzt.f1Hub.instraestructure.dto.in.offer.OfferInDto;
import ptzt.f1Hub.instraestructure.dto.in.offer.OfferInUpdateDto;
import ptzt.f1Hub.instraestructure.dto.out.marketItems.MarketItemsOutDto;
import ptzt.f1Hub.instraestructure.dto.out.offer.OfferOutDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.DefaultResponseDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.PageOutDto;

import java.util.Optional;

@RestController
@RequestMapping("/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferMapper offerMapper;

    private final OfferService offerService;
    private final AppUserService appUserService;
    private final AccountService accountService;
    private final MarketItemService marketItemService;
    private final LeagueService leagueService;

    @GetMapping
    public ResponseEntity<PageOutDto<OfferOutDto>> getAllByUser(@PageableDefault Pageable pageable, Authentication authentication){

        return ResponseEntity.ok(
                new PageOutDto<>(
                        offerService.getAllByUser(pageable, appUserService.getByAccount(
                                accountService.getByEmail(authentication.getName()))
                        ).map(offerMapper::toOutDto)
                )
        );

    }

    @GetMapping("/{leagueId}/league/{itemId}/item")
    public ResponseEntity<OfferOutDto> getOffer(Authentication authentication,
                                                      @PathVariable Long leagueId,
                                                      @PathVariable Long itemId){

        Optional<Offer> opOffer = offerService.getOfferByMarketItemAndAppUserAndLeague(
            appUserService.getByAccount(accountService.getByEmail(authentication.getName())),
            leagueService.getById(leagueId),
            marketItemService.getById(itemId)
        );

        return opOffer.map(offer ->
                    ResponseEntity.ok(offerMapper.toOutDto(offer))).orElseGet(() -> ResponseEntity.noContent().build()
               );

    }

    @PostMapping
    public ResponseEntity<OfferOutDto> create(@Valid @RequestBody OfferInDto offerInDto){

        return ResponseEntity.ok(
                    offerMapper.toOutDto(
                            offerService.create(offerMapper.toEntity(offerInDto))
                    )
        );

    }

    @PutMapping("/{id}")
    public ResponseEntity<DefaultResponseDto> update(@PathVariable Long id, @Valid @RequestBody OfferInUpdateDto offerInUpdateDto){

        Offer entity = offerService.getById(id);
        offerMapper.toUpdate(offerInUpdateDto, entity);

        offerService.update(entity);

        return ResponseEntity.ok(
                new DefaultResponseDto(200, "Offer has been updated")
        );

    }

}
