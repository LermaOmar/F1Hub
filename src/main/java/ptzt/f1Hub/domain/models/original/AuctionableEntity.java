package ptzt.f1Hub.domain.models.original;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ptzt.f1Hub.domain.models.original.market.MarketItem;

import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
@Getter
@Setter
public abstract class AuctionableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nationality;

    private Long points = 0L;

    private Long previousPoints = 0L;

    private Long price;

    private Boolean active = true;

    private String imageUrl = "";

    @OneToMany(mappedBy = "auctionableEntity")
    private Set<MarketItem> marketItems = new HashSet<>();


}
