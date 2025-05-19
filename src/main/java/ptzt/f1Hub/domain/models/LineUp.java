package ptzt.f1Hub.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class LineUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "LineUp_drivers",
            joinColumns = @JoinColumn(name = "lineUp_id"),
            inverseJoinColumns = @JoinColumn(name = "drivers_id"))
    private Set<Driver> drivers = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    private Long totalPoints = 0L;

}