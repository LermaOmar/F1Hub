package ptzt.f1Hub.instraestructure.dto.out.auctionableEntities;

public record TeamOutLimitedDto(

        Long id,

        String name,

        String nationality,

        Long price,

        Boolean active,

        String type,

        String imageUrl

) implements AuctionableEntityOutDto {
}
