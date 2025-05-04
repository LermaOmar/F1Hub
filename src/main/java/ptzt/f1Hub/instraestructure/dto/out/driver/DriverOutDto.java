package ptzt.f1Hub.instraestructure.dto.out.driver;

public record DriverOutDto(

        Long id,

        String name,

        String nationality,

        Long price,

        Long points,

        Boolean active

) {
}
