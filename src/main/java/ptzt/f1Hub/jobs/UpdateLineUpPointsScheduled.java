package ptzt.f1Hub.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ptzt.f1Hub.application.services.lineUp.LineUpService;


@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateLineUpPointsScheduled {

    private final LineUpService lineUpService;

    @Scheduled(cron = "0 59 23 * * 1")
    public void calculateLineUpPoints(){


        lineUpService.getAll().forEach(lineUp -> {

            if (lineUp.getTeam() != null) {

                lineUp.setTotalPoints(lineUp.getTotalPoints() + (lineUp.getTeam().getPoints() - lineUp.getTeam().getPreviousPoints()));
            }

            lineUp.getDrivers().forEach(driver -> lineUp.setTotalPoints(lineUp.getTotalPoints() + (driver.getPoints() - driver.getPreviousPoints())));

            lineUpService.update(lineUp);

        });
        log.info("====================================");
        log.info("All LineUps points have been updated");
        log.info("====================================");
    }

}
