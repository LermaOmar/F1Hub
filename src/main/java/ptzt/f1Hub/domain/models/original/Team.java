package ptzt.f1Hub.domain.models.original;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Entity
@DiscriminatorValue("Team")
@Getter
@Setter
public class Team extends AuctionableEntity {

    @Column(unique = true)
    private String name;


    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
    private Set<LineUp> lineUps = new HashSet<>();

}