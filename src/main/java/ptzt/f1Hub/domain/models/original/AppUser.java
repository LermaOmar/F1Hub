package ptzt.f1Hub.domain.models.original;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ptzt.f1Hub.domain.models.original.market.Offer;

import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "appUser", fetch = FetchType.EAGER)
    private Set<LineUp> lineUps = new HashSet<>();

    @OneToMany(mappedBy = "appUser",fetch = FetchType.LAZY)
    private Set<Offer> offers = new HashSet<>();

    @OneToMany(mappedBy = "appUser",fetch = FetchType.LAZY)
    private Set<Budget> budgets = new HashSet<>();


}