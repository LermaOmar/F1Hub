package ptzt.f1Hub.domain.models.copy;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(
        name = "lineup",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_lineup_league_team",
                columnNames = {"league_id", "team_id"}
        )
)
public class LineUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "LineUp_drivers",
            joinColumns = @JoinColumn(name = "lineup_id"),
            inverseJoinColumns = @JoinColumn(name = "driver_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_lineup_driver",
                    columnNames = {"lineup_id", "driver_id"}
            )
    )
    private Set<Driver> drivers = new HashSet<>();

    @Column(name = "total_points")
    private Long totalPoints = 0L;
}
