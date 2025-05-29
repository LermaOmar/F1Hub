package ptzt.f1Hub.domain.models.copy;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("Driver")
@Getter
@Setter
public class Driver extends AuctionableEntity {

    private String name;

    @ManyToMany(mappedBy = "drivers", fetch = FetchType.LAZY)
    private Set<LineUp> lineUps = new HashSet<>();





}
