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

        List<Market> markets = marketService.getAll();
        if (markets.isEmpty())
            return;

        List<MarketItem> availableItems = marketItemService.getAll().stream()
                .filter(marketItem -> marketItem.getAvailable() && marketItem.getMarkets().size() == markets.size())
                .toList();

        List<MarketItem> unavailableItems = marketItemService.getAll().stream()
                .filter(item -> !item.getAvailable())
                .collect(Collectors.toList());


        availableItems.forEach(marketItem -> marketItemService.hideInMarket(marketItem,markets));

        Collections.shuffle(unavailableItems);

        Set<MarketItem> selectedItems = new HashSet<>(unavailableItems.subList(0, Math.min(7, unavailableItems.size())));

        selectedItems.forEach(item -> {
            marketItemService.displayInMarket(item,markets);
        });


    }
}
