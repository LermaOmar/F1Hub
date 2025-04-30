package ptzt.f1Hub.application.services.appUser;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ptzt.f1Hub.domain.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.models.AppUser;
import ptzt.f1Hub.instraestructure.repository.AppUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService{

    private final AppUserRepository appUserRepository;

    @Override
    public AppUser create(AppUser appUser) {

        return appUserRepository.save(appUser);

    }

    @Override
    public AppUser update(AppUser appUser) {

        return appUserRepository.save(appUser);

    }

    @Override
    public void delete(AppUser appUser) {

        appUserRepository.delete(appUser);

    }

    @Override
    public AppUser getById(Long id) {

        return appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no user with that id"));

    }

    @Override
    public Page<AppUser> getAll(Pageable pageable) {

        return appUserRepository.findAll(pageable);

    }

}
