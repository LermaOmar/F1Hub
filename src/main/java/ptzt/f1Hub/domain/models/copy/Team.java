package ptzt.f1Hub.domain.models.copy;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
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