package ptzt.f1Hub.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "league", fetch = FetchType.LAZY)
    private List<LineUp> lineUps;

    @Column(unique = true)
    private String name;

}