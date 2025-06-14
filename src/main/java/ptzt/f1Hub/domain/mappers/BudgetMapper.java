package ptzt.f1Hub.domain.mappers;

import org.mapstruct.Mapper;
import ptzt.f1Hub.domain.models.original.Budget;
import ptzt.f1Hub.instraestructure.dto.out.budget.BudgetOutDto;

@Mapper(componentModel = "spring")

public interface BudgetMapper {

    BudgetOutDto toOutDto(Budget budget);

}
