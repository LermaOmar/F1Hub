package ptzt.f1Hub.application.services.offer;

import ptzt.f1Hub.domain.models.market.Offer;

import java.util.List;

public interface OfferService {

    List<Offer> getAll();

    Offer getById(Long id);

    Offer create(Offer offer);

    Offer update(Offer offer);

    void delete(Offer offer);


}
