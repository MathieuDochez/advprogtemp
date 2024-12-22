package fact.it.reviewservice.repository;

import fact.it.reviewservice.model.Review;
import jakarta.transaction.Transactional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface ReviewRepository extends MongoRepository<Review, String> {
}
