package ptzt.f1Hub.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ptzt.f1Hub.application.services.driver.DriverService;
import ptzt.f1Hub.application.services.team.TeamService;


@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateValueScheduled {

    private final DriverService driverService;
    private final TeamService teamService;



    @Scheduled(cron = "0 0 0 * * *")
    public void calculateTeamsValue(){

        teamService.getAll().forEach(teamService::updateValue);
        log.info("=================================");
        log.info("All Teams values has been updated");
        log.info("=================================");

    }

    @Scheduled(cron = "0 0 0 * * *")
    public void calculateDriversValue(){

        driverService.getAll().forEach(driverService::updateValue);
        log.info("===================================");
        log.info("All Drivers values has been updated");
        log.info("===================================");

    }
}
