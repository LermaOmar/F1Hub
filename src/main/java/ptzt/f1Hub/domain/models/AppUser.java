package ptzt.f1Hub.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String password;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    @OneToMany(mappedBy = "appUser")
    private List<LineUp> lineUps;

}