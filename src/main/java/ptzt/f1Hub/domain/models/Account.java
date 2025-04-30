package ptzt.f1Hub.domain.models;

import jakarta.persistence.*;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import ptzt.f1Hub.domain.enums.Roles;

@Entity
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String password;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    boolean active;

    @Enumerated
    Roles rol;

    @OneToOne(mappedBy = "account")
    private AppUser appUser;

}
