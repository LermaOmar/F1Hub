package ptzt.f1Hub.domain.models.market;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ptzt.f1Hub.domain.models.League;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "league_id", unique = true)
    private League league;

}
