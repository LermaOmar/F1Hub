package ptzt.f1Hub.application.services.auctionableEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ptzt.f1Hub.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.models.original.AuctionableEntity;
import ptzt.f1Hub.instraestructure.repository.original.AuctionableEntityRepository;

@Service
@RequiredArgsConstructor
public class AuctionableEntityServiceImpl implements AuctionableEntityService{

    private final AuctionableEntityRepository auctionableEntityRepository;


    @Override
    public void updatePoints(Long id, Long points) {

        AuctionableEntity auctionableEntity = getById(id);

        auctionableEntity.setPreviousPoints(auctionableEntity.getPoints());
        auctionableEntity.setPoints(auctionableEntity.getPoints() + points);
        auctionableEntityRepository.save(auctionableEntity);

    }

    private AuctionableEntity getById(Long id){

        return auctionableEntityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no AuctionableEntity with that ID"));

    }
}
