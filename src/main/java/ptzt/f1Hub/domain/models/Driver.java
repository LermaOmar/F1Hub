package ptzt.f1Hub.domain.models;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("Driver")
@Getter
@Setter
public class Driver extends AuctionableEntity{

    private String name;

    @ManyToMany(mappedBy = "drivers", fetch = FetchType.LAZY)
    private Set<LineUp> lineUps = new HashSet<>();





}
