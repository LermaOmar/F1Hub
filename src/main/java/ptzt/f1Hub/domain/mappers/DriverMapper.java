package ptzt.f1Hub.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ptzt.f1Hub.domain.models.Driver;
import ptzt.f1Hub.instraestructure.dto.in.driver.DriverInDto;
import ptzt.f1Hub.instraestructure.dto.out.driver.DriverOutDto;
import ptzt.f1Hub.instraestructure.dto.out.driver.DriverOutLimitedDto;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "marketItems", ignore = true)
    @Mapping(target = "points", ignore = true)
    @Mapping(target = "previousPoints", ignore = true)
    @Mapping(target = "lineUps", ignore = true)
    Driver toEntity(DriverInDto driverInDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "marketItems", ignore = true)
    @Mapping(target = "points", ignore = true)
    @Mapping(target = "previousPoints", ignore = true)
    @Mapping(target = "lineUps", ignore = true)
    void toUpdate(DriverInDto driverInDto, @MappingTarget Driver driver);

    DriverOutDto toOutDto(Driver driver);

    @Mapping(target = "type", expression = "java(\"Driver\")")
    DriverOutLimitedDto toOutLimitedDto(Driver driver);
}
