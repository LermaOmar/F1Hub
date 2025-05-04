package ptzt.f1Hub.domain.models.market;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ptzt.f1Hub.domain.models.AppUser;
import ptzt.f1Hub.domain.models.AuctionableEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    private Long amount;

    @ManyToOne
    @JoinColumn(name = "market_item_id")
    private MarketItem marketItem;

    private LocalDateTime createdAt;

}
