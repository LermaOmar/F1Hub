package ptzt.f1Hub.instraestructure.dto.batch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MarketItemJoin {

    private Long id;

    private Boolean available;

    private Long aeId;

    private String aeType;

}
