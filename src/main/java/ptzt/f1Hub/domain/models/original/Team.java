package ptzt.f1Hub.domain.models.original;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@DiscriminatorValue("Team")
@Getter
@Setter
public class Team extends AuctionableEntity {

    @Column(unique = true)
    private String name;

    @OneToOne(mappedBy = "team")
    private LineUp lineUp;

}