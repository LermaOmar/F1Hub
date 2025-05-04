package ptzt.f1Hub.domain.models.market;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ptzt.f1Hub.domain.models.AuctionableEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class MarketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "auctionable_entity_id")
    private AuctionableEntity auctionableEntity;

    @OneToMany(mappedBy = "marketItem")
    private Set<Offer> offers = new HashSet<>();

    private Boolean available = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "market_id")
    private Market market;

}
