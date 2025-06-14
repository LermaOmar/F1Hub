package ptzt.f1Hub.config;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import ptzt.f1Hub.application.services.market.item.MarketItemService;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MarketConfig {

    private final MarketItemService marketItemService;


    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void setUpMarketOnStartup() {

        marketItemService.updateMarketItems();

    }

    //Scheduled(cron = "*/30 * * * * *")
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void setUpMarketDaily() {

        marketItemService.updateMarketItems();
        log.info("====================");
        log.info("Market items updated");
        log.info("====================");

    }



}
