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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long points = 0L;

    private Long previousPoints = 0L;

    private Long price;

    private Boolean active = true;

    @ManyToMany(mappedBy = "drivers")
    private List<LineUp> lineUps;

    private String nationality;

    @PreUpdate
    public void preUpdate(){

        this.previousPoints = this.points;

    }



}
