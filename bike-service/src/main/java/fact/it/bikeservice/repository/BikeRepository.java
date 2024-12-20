package fact.it.bikeservice.repository;

import fact.it.bikeservice.model.Bike;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BikeRepository extends MongoRepository<Bike, String> {
    List<Bike> findBySkuCodeIn(List<String> skuCode);
}
