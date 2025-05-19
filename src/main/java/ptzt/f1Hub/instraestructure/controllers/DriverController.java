package ptzt.f1Hub.instraestructure.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ptzt.f1Hub.application.services.auctionableEntity.AuctionableEntityService;
import ptzt.f1Hub.application.services.driver.DriverService;
import ptzt.f1Hub.domain.mappers.DriverMapper;
import ptzt.f1Hub.domain.models.Driver;
import ptzt.f1Hub.instraestructure.dto.in.driver.DriverInDto;
import ptzt.f1Hub.instraestructure.dto.out.driver.DriverOutDto;
import ptzt.f1Hub.instraestructure.dto.out.driver.DriverOutLimitedDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.DefaultResponseDto;
import ptzt.f1Hub.instraestructure.dto.out.shared.PageOutDto;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private final DriverMapper driverMapper;
    private final AuctionableEntityService auctionableEntityService;

    @GetMapping
    public ResponseEntity<PageOutDto<DriverOutLimitedDto>> getAll(@PageableDefault Pageable pageable,
                                                                @RequestParam(required = false) boolean skipNotActive){

        Page<Driver> page = skipNotActive ? driverService.getAllActive(pageable) : driverService.getAll(pageable);

        return ResponseEntity.ok(
                new PageOutDto<>(page.map(driverMapper::toOutLimitedDto))
        ) ;

    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverOutDto> getById(@PathVariable Long id){

        return ResponseEntity.ok(
                driverMapper.toOutDto(
                        driverService.getById(id)
                )
        ) ;

    }

    @PostMapping
    public ResponseEntity<DriverOutDto> create(@Valid @RequestBody DriverInDto driverInDto){

        return ResponseEntity.ok(
                driverMapper.toOutDto(
                        driverService.create(driverMapper.toEntity(driverInDto))
                )
        );

    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverOutDto> update(@Valid @RequestBody DriverInDto driverInDto, @PathVariable Long id){

        Driver driver = driverService.getById(id);

        driverMapper.toUpdate(driverInDto,driver);

        return ResponseEntity.ok(
                driverMapper.toOutDto(
                        driverService.update(driver)
                )
        );

    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<DefaultResponseDto> deactivate(@PathVariable Long id){

        driverService.deactivate(id);

        return ResponseEntity.ok(
                new DefaultResponseDto(200, "Driver has been deactivated")
        );

    }

    @PutMapping("/points/{id}")
    public ResponseEntity<DefaultResponseDto> setPoints(@PathVariable Long id, @RequestParam(name = "points") Long points){

        auctionableEntityService.updatePoints(id,points);

        return ResponseEntity.ok(
                new DefaultResponseDto(200, "Driver's points have been updated")
        );

    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<DefaultResponseDto> activate(@PathVariable Long id){

        driverService.activate(id);

        return ResponseEntity.ok(
                new DefaultResponseDto(200, "Driver has been activated")
        );

    }

}
