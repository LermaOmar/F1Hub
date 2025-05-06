package ptzt.f1Hub.instraestructure.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ptzt.f1Hub.application.services.market.MarketService;
import ptzt.f1Hub.application.services.offer.OfferService;
import ptzt.f1Hub.domain.mappers.OfferMapper;
import ptzt.f1Hub.domain.models.market.Offer;
import ptzt.f1Hub.instraestructure.dto.in.offer.OfferInDto;
import ptzt.f1Hub.instraestructure.dto.in.offer.OfferInUpdateDto;
import ptzt.f1Hub.instraestructure.dto.out.offer.OfferOutDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.DefaultResponseDto;

@RestController
@RequestMapping("/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;
    private final OfferMapper offerMapper;

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
