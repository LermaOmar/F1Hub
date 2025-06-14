package ptzt.f1Hub.instraestructure.dto.batch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriverAuctionableJoin {

    private Long id;

    private Boolean active;

    private String imageUrl;

    private String nationality;

    private Long points;

    private Long previousPoints;

    private Long price;

    private String driverName;

}
