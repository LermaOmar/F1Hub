package ptzt.f1Hub.jobs;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ptzt.f1Hub.application.services.driver.DriverService;
import ptzt.f1Hub.application.services.team.TeamService;


@Component
@RequiredArgsConstructor

public class UpdateValueScheduled {

    private final DriverService driverService;
    private final TeamService teamService;



    @Scheduled(cron = "0 59 23 * * 1")
    public void calculateTeamsValue(){

        teamService.getAll().forEach(teamService::updateValue);

    }

    @Scheduled(cron = "0 59 23 * * 1")
    public void calculateDriversValue(){

        driverService.getAll().forEach(driverService::updateValue);

    }
}
