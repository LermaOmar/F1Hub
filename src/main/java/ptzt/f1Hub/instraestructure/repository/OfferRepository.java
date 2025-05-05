package ptzt.f1Hub.instraestructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptzt.f1Hub.domain.models.market.Offer;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
}