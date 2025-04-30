package ptzt.f1Hub.application.services.driver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ptzt.f1Hub.domain.models.Driver;

import java.util.List;

public interface DriverService {

    Driver create(Driver driver);

    Driver update(Driver driver);

    Driver getById(Long id);

    Page<Driver> getAll(Pageable pageable);

    List<Driver> getAll();

    void deactivate(Long id);

    void updateValue(Driver driver);

}
