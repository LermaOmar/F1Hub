package ptzt.f1Hub.instraestructure.dto.out.team;

public record TeamOutDto(

        Long id,

        String name,

        String nationality,

        Long price,

        Long points,

        Long previousPoints,

        Boolean active,

        String imageUrl

) {
}
