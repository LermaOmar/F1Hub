package ptzt.f1Hub.domain.models.copy;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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


    @OneToMany(mappedBy = "team", orphanRemoval = true)
    private Set<LineUp> lineUps = new HashSet<>();

}