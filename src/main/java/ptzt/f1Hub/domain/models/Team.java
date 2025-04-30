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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean active = true;

    @Column(unique = true)
    private String name;

    private Long points = 0L;

    private Long previousPoints = 0L;

    private Long price;

    private String nationality;

    @OneToOne(mappedBy = "team")
    private LineUp lineUp;

    @PreUpdate
    public void preUpdate(){

        this.previousPoints = this.points;

    }
}