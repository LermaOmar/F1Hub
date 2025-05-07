package ptzt.f1Hub.config.security;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ptzt.f1Hub.domain.enums.Roles;
import ptzt.f1Hub.domain.models.Account;
import ptzt.f1Hub.instraestructure.repository.AccountRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppUserDetailService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account person = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        return User.builder()
                .username(person.getUsername())
                .password(person.getPassword())
                .roles(getRoles(person.getRoles()))
                .build();
    }

    public UserDetails loadUserByJwtUsername(String username) throws UsernameNotFoundException {
        try{

            Account person = accountRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException(username));

            return User.builder()
                    .username(person.getUsername())
                    .password(person.getPassword())
                    .roles(getRoles(person.getRoles()))
                    .build();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String[] getRoles(Set<Roles> roles) {

        List<String> rolesList = roles.stream()
                .map(Roles::toString)
                .toList();

        String[] arrayRoles = new String[rolesList.size()];

        for (int i = 0; i < rolesList.size(); i++)
            arrayRoles[i] = rolesList.get(i);


        return arrayRoles;
    }
}