package ptzt.f1Hub.domain.models;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private int driverNumber;

    private long points;

    private boolean competing;

    @ManyToMany(mappedBy = "drivers")
    private List<LineUp> lineUps;

    private String nationality;

}
