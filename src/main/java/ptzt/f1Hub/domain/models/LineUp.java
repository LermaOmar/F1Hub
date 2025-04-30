package ptzt.f1Hub.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @ManyToMany
    @JoinTable(name = "LineUp_drivers",
            joinColumns = @JoinColumn(name = "lineUp_id"),
            inverseJoinColumns = @JoinColumn(name = "drivers_id"))
    private List<Driver> drivers;

    @OneToOne
    @JoinColumn(name = "team_id")
    private Team team;

}