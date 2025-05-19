package ptzt.f1Hub.instraestructure.dto.out.driver;

public record DriverOutLimitedDto(

        Long id,

        String name,

        String nationality,

        Long price,

        Boolean active,

        String type


) {
}
