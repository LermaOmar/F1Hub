package ptzt.f1Hub.application.services.driver;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptzt.f1Hub.application.services.lineUp.LineUpService;
import ptzt.f1Hub.application.services.market.item.MarketItemService;
import ptzt.f1Hub.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.models.Driver;
import ptzt.f1Hub.domain.models.market.MarketItem;
import ptzt.f1Hub.instraestructure.repository.DriverRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService{

    private final DriverRepository driverRepository;
    private final LineUpService lineUpService;
    private final MarketItemService marketItemService;

    @Transactional
    @Override
    public Driver create(Driver driver) {

        Driver createdDriver = driverRepository.save(driver);

        MarketItem marketItem = new MarketItem();
        marketItem.setAuctionableEntity(driver);

        marketItemService.create(marketItem);

        return createdDriver;

    }

    @Override
    public Driver update(Driver driver) {

        return driverRepository.save(driver);

    }

    @Override
    public Driver getById(Long id) {

        return driverRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("There is no driver with that ID"));

    }

    @Override
    public Page<Driver> getAll(Pageable pageable) {

        return driverRepository.findAll(pageable);

    }

    @Override
    public Page<Driver> getAllActive(Pageable pageable) {

        return driverRepository.findAllByActiveTrue(pageable);

    }

    @Override
    public List<Driver> getAll() {

        return driverRepository.findAll();

    }

    @Override
    public List<Driver> getAllNotAssigned(Long league) {

        return driverRepository.findAllByNotAssignedToLeague(league);

    }

    @Transactional
    @Override
    public void deactivate(Long id) {

        Driver driver = getById(id);

        driver.setActive(false);

        lineUpService.getAllByDriver(List.of(driver)).forEach(lineUp -> {

            Set<Driver> drivers = lineUp.getDrivers();

            lineUp.setDrivers(drivers.stream()
                    .filter(driv -> !driv.getId().equals(driver.getId()))
                    .collect(Collectors.toSet())
            );

            lineUpService.update(lineUp);

        });

        driverRepository.save(driver);
    }


    @Transactional
    @Override
    public void activate(Long id) {

        Driver driver = getById(id);

        driver.setActive(true);

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
