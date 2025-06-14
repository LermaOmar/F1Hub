package ptzt.f1Hub.instraestructure.dto.out.auctionableEntities;

public record DriverOutDto(

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
