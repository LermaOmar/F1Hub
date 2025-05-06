
package ptzt.f1Hub.instraestructure.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ptzt.f1Hub.application.services.market.MarketService;
import ptzt.f1Hub.application.services.market.item.MarketItemService;
import ptzt.f1Hub.domain.mappers.LeagueMapper;
import ptzt.f1Hub.domain.models.market.Market;
import ptzt.f1Hub.instraestructure.dto.in.league.LeagueInIdDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.DefaultResponseDto;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/marketItems")
@RequiredArgsConstructor
public class MarketItemController {

    private final MarketItemService marketItemService;
    private final MarketService marketService;
    private final LeagueMapper leagueMapper;

    @PutMapping("/display/{id}")
    public ResponseEntity<DefaultResponseDto> displayInMarket(@PathVariable Long id, @Valid @RequestBody LeagueInIdDto leagueInIdDto){

        List<Market> markets = new ArrayList<>();
        markets.add(marketService.getByLeague(leagueMapper.toEntity(leagueInIdDto)));

        marketItemService.displayInMarket(marketItemService.getById(id),markets);

        return ResponseEntity.ok(
                new DefaultResponseDto(200, "Item has been displayed in the market")
        );

    }

    @PutMapping("/hide/{id}")
    public ResponseEntity<DefaultResponseDto> hideInMarket(@PathVariable Long id, @Valid @RequestBody LeagueInIdDto leagueInIdDto){

        List<Market> markets = new ArrayList<>();
        markets.add(marketService.getByLeague(leagueMapper.toEntity(leagueInIdDto)));
        marketItemService.hideInMarket(marketItemService.getById(id),markets);

        return ResponseEntity.ok(
                new DefaultResponseDto(200, "Item has been hiden in the market")
        );

    }

}
