package ptzt.f1Hub.application.services.offer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ptzt.f1Hub.domain.models.original.AppUser;
import ptzt.f1Hub.domain.models.original.market.Offer;

import java.util.List;

public interface OfferService {

    List<Offer> getAll();

    Page<Offer> getAllByUser(Pageable pageable, AppUser appUser);

    Offer getById(Long id);

    Offer create(Offer offer);

    Offer update(Offer offer);

    void delete(Offer offer);


}
