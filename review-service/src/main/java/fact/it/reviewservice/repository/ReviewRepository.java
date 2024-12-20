package fact.it.reviewservice.repository;

import fact.it.reviewservice.model.Review;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface ReviewRepository extends JpaRepository<Review, String> {
}
