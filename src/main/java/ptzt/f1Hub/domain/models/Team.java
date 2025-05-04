package ptzt.f1Hub.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Team extends AuctionableEntity {

    @Column(unique = true)
    private String name;

    @OneToOne(mappedBy = "team")
    private LineUp lineUp;

}