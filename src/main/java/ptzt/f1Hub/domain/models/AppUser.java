package ptzt.f1Hub.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ptzt.f1Hub.domain.models.market.Offer;

import java.util.*;

@Entity
@Getter
@Setter
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY)
    private Set<LineUp> lineUps = new HashSet<>();

    @OneToMany(mappedBy = "appUser", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private Set<Offer> offers = new HashSet<>();

    @OneToMany(mappedBy = "appUser", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private Set<Budget> budgets = new HashSet<>();

}