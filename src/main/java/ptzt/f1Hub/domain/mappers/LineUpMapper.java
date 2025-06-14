package ptzt.f1Hub.domain.mappers;

import org.mapstruct.Mapper;
import ptzt.f1Hub.domain.models.original.LineUp;
import ptzt.f1Hub.instraestructure.dto.out.lineUp.LineUpOutDto;

@Mapper(componentModel = "spring")
public interface LineUpMapper {

    LineUpOutDto toOutDto(LineUp lineUp);

}
