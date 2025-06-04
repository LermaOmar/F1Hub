package ptzt.f1Hub.domain.models.copy;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ptzt.f1Hub.domain.models.copy.market.Market;
import ptzt.f1Hub.domain.models.copy.market.Offer;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "league", fetch = FetchType.EAGER)
    private Set<LineUp> lineUps = new HashSet<>();

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "league", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Offer> offers = new HashSet<>();

    @OneToMany(mappedBy = "league", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Budget> budgets = new HashSet<>();

    @OneToOne(mappedBy = "league", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Market market;

}