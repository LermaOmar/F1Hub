package ptzt.f1Hub.application.services.driver;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptzt.f1Hub.application.services.lineUp.LineUpService;
import ptzt.f1Hub.domain.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.models.Driver;
import ptzt.f1Hub.instraestructure.repository.DriverRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService{

    private final DriverRepository driverRepository;
    private final LineUpService lineUpService;

    @Transactional
    @Override
    public Driver create(Driver driver) {

        return driverRepository.save(driver);

    }

    @Override
    public Driver update(Driver driver) {

        return driverRepository.save(driver);

    }

    @Override
    public Driver getById(Long id) {

        return driverRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("No se ha encontrado un piloto con ese ID"));

    }

    @Override
    public Page<Driver> getAll(Pageable pageable) {

        return driverRepository.findAll(pageable);

    }

    @Override
    public List<Driver> getAll() {

        return driverRepository.findAll();

    }

    @Transactional
    @Override
    public void deactivate(Long id) {

        Driver driver = getById(id);

        driver.setActive(false);

        lineUpService.getAllByDriver(driver).forEach(lineUp -> {

            List<Driver> drivers = lineUp.getDrivers();

            lineUp.setDrivers(drivers.stream()
                    .filter(driv -> !driv.getId().equals(driver.getId()))
                    .toList()
            );

            lineUpService.update(lineUp);

        });

        driverRepository.save(driver);
    }

    @Transactional
    @Override
    public void updateValue(Driver driver) {

        if (driver.getPreviousPoints() == 0)
            return;

        long pointsDifference = driver.getPoints() - driver.getPreviousPoints();

        double priceChangeFactor =  pointsDifference * 1.0 / driver.getPreviousPoints();

        driver.setPrice(Math.round(driver.getPrice() + (driver.getPrice() * priceChangeFactor)));

    }
}
