package ptzt.f1Hub.domain.models.copy.market;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ptzt.f1Hub.domain.models.copy.AppUser;
import ptzt.f1Hub.domain.models.copy.League;
import ptzt.f1Hub.domain.models.copy.market.MarketItem;

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

    @ManyToOne( cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "market_item_id")
    private MarketItem marketItem;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

    @PrePersist
    public void prePersist(){

        this.createdAt  = LocalDateTime.now();

    }

    @PreUpdate
    public void preUpdate(){

        this.createdAt  = LocalDateTime.now();

    }

}
