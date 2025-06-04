package ptzt.f1Hub.domain.models.copy;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ptzt.f1Hub.domain.models.copy.market.Offer;

import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(mappedBy = "appUser", orphanRemoval = true, cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<Offer> offers = new HashSet<>();

    @OneToMany(mappedBy = "appUser", orphanRemoval = true, cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<Budget> budgets = new HashSet<>();


}