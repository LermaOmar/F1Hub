package ptzt.f1Hub.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean competing;

    @Column(unique = true)
    private String name;

    private long points;

    private String nationality;

    @OneToMany(mappedBy = "team", orphanRemoval = true)
    private List<League> leagues;

}