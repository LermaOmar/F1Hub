package ptzt.f1Hub.config;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import ptzt.f1Hub.application.services.market.item.MarketItemService;

@Configuration
@RequiredArgsConstructor
public class MarketConfig {

    private final MarketItemService marketItemService;


    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void setUpMarketOnStartup() {

        marketItemService.updateMarketItems();

    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void setUpMarketDaily() {

        marketItemService.updateMarketItems();

    }



}
