package ptzt.f1Hub.instraestructure.dto.batch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class TeamAuctionableJoin {

    private Long id;

    private Boolean active;

    private String imageUrl;

    private String nationality;

    private Long points;

    private Long previousPoints;

    private Long price;

    private String teamName;

}
