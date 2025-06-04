package ptzt.f1Hub.domain.models.copy;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ptzt.f1Hub.domain.enums.Roles;

import java.util.HashSet;
import java.util.Set;

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

    boolean active = false;

    @ElementCollection(targetClass = Roles.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "account_roles", joinColumns = @JoinColumn(name = "account_id"))
    @Enumerated(EnumType.STRING)
    Set<Roles> roles = Set.of(Roles.PLAYER);

    @OneToOne(mappedBy = "account", cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private AppUser appUser;

    @OneToMany(mappedBy = "account", orphanRemoval = true, cascade = {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.EAGER)
    private Set<VerificationToken> verificationTokens = new HashSet<>();

}
