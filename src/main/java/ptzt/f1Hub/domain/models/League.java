package ptzt.f1Hub.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "league", fetch = FetchType.LAZY)
    private Set<LineUp> lineUps = new HashSet<>();

    @Column(unique = true)
    private String name;

}