package ptzt.f1Hub.jobs;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ptzt.f1Hub.application.services.driver.DriverService;
import ptzt.f1Hub.application.services.lineUp.LineUpService;
import ptzt.f1Hub.application.services.team.TeamService;


@Component
@RequiredArgsConstructor

public class UpdateLineUpPointsScheduled {

    private final LineUpService lineUpService;


    @Scheduled(cron = "0 59 23 * * 1")
    public void calculateTeamsValue(){


        lineUpService.getAll().forEach(lineUp -> {

            if (lineUp.getTeam() != null || lineUp.getDrivers().size() == 2){

                lineUp.setTotalPoints(lineUp.getTotalPoints() + lineUp.getTeam().getPoints());

                lineUp.getDrivers().forEach(driver -> lineUp.setTotalPoints(lineUp.getTotalPoints() + driver.getPoints()));
            }

        });

    }

}
