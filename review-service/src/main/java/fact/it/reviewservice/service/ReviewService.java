package fact.it.reviewservice.service;

import fact.it.reviewservice.dto.ReviewRequest;
import fact.it.reviewservice.dto.ReviewResponse;
import fact.it.reviewservice.model.Review;
import fact.it.reviewservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public void createReview(ReviewRequest reviewRequest) {
        Review review = Review.builder()
                .comment(reviewRequest.getComment())
                .rating(reviewRequest.getRating())
                .build();

        reviewRepository.save(review);
    }

    public List<ReviewResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();

        return reviews.stream().map(this::mapToReviewResponse).toList();
    }

    public void updateReview(String id, ReviewRequest reviewRequest) {
        Optional<Review> optionalReview = reviewRepository.findById(id);

        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setComment(reviewRequest.getComment());
            review.setRating(reviewRequest.getRating());

            reviewRepository.save(review);
        } else {
            throw new IllegalArgumentException("Review with id " + id + " not found");
        }
    }

    public void deleteReview(String id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Review with id " + id + " not found");
        }
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .comment(review.getComment())
                .rating(review.getRating())
                .build();
    }
}
