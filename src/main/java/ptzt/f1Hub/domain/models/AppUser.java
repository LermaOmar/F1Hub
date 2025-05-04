package ptzt.f1Hub.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY)
    private Set<LineUp> lineUps = new HashSet<>();

    private Long budget = 100_000_000L;

}