package ptzt.f1Hub.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import ptzt.f1Hub.application.services.market.item.MarketItemService;
import ptzt.f1Hub.application.services.market.MarketService;
import ptzt.f1Hub.domain.models.market.Market;
import ptzt.f1Hub.domain.models.market.MarketItem;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class MarketConfig {

    private final MarketItemService marketItemService;
    private final MarketService marketService;

    @EventListener(ApplicationReadyEvent.class)
    public void setUpMarketOnStartup() {

        updateMarketItems();

    }

    @Scheduled(cron = "0 0 0 * * *")
    public void setUpMarketDaily() {

        updateMarketItems();

    }

    private void updateMarketItems() {

        Optional<Market> opMarket = marketService.getById(1L);

        Market market = opMarket.orElseGet(() -> marketService.create(new Market()));

        List<MarketItem> availableItems = marketItemService.getAll().stream()
                .filter(item -> !market.getMarketItems().contains(item))
                .collect(Collectors.toList());

        List<MarketItem> unavailableItems = marketItemService.getAll().stream()
                .filter(item -> item.getMarket() != null)
                .toList();


        unavailableItems.forEach(marketItemService::hideInMarket);

        Collections.shuffle(availableItems);

        Set<MarketItem> selectedItems = new HashSet<>(availableItems.subList(0, Math.min(7, availableItems.size())));

        selectedItems.forEach(item -> {
            item.setMarket(market);
            marketItemService.displayInMarket(item);
        });

        market.getMarketItems().clear();
        market.setMarketItems(selectedItems);

        marketService.update(market);

    }
}
