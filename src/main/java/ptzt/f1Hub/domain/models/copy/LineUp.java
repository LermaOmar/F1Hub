package ptzt.f1Hub.domain.models.copy;

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

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "league_id")
    private League league;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name = "LineUp_drivers",
            joinColumns = @JoinColumn(name = "lineUp_id"),
            inverseJoinColumns = @JoinColumn(name = "drivers_id"))
    private Set<Driver> drivers = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "total_points")
    private Long totalPoints = 0L;

}