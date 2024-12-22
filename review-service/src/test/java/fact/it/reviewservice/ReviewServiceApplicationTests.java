package fact.it.reviewservice;

import fact.it.reviewservice.dto.ReviewRequest;
import fact.it.reviewservice.dto.ReviewResponse;
import fact.it.reviewservice.model.Review;
import fact.it.reviewservice.repository.ReviewRepository;
import fact.it.reviewservice.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ReviewServiceApplicationTests {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReview() {
        // Arrange
        ReviewRequest reviewRequest = ReviewRequest.builder()
                .comment("Great product!")
                .rating(5)
                .build();
        Review review = Review.builder()
                .comment("Great product!")
                .rating(5)
                .build();

        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        reviewService.createReview(reviewRequest);

        // Assert
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testGetAllReviews() {
        // Arrange
        Review review1 = Review.builder().id("1").comment("Great!").rating(5).build();
        Review review2 = Review.builder().id("2").comment("Not bad").rating(3).build();
        when(reviewRepository.findAll()).thenReturn(List.of(review1, review2));

        // Act
        List<ReviewResponse> reviewResponses = reviewService.getAllReviews();

        // Assert
        assertEquals(2, reviewResponses.size());
        assertEquals("Great!", reviewResponses.get(0).getComment());
        assertEquals(5, reviewResponses.get(0).getRating());
        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void testUpdateReviewSuccess() {
        // Arrange
        String id = "1";
        Review existingReview = Review.builder().id(id).comment("Good").rating(4).build();
        ReviewRequest updateRequest = ReviewRequest.builder().comment("Great!").rating(5).build();

        when(reviewRepository.findById(id)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(existingReview)).thenReturn(existingReview);

        // Act
        reviewService.updateReview(id, updateRequest);

        // Assert
        assertEquals("Great!", existingReview.getComment());
        assertEquals(5, existingReview.getRating());
        verify(reviewRepository, times(1)).findById(id);
        verify(reviewRepository, times(1)).save(existingReview);
    }

    @Test
    void testUpdateReviewNotFound() {
        // Arrange
        String id = "1";
        ReviewRequest updateRequest = ReviewRequest.builder().comment("Great!").rating(5).build();

        when(reviewRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.updateReview(id, updateRequest);
        });
        assertEquals("Review with id 1 not found", exception.getMessage());
        verify(reviewRepository, times(1)).findById(id);
        verify(reviewRepository, times(0)).save(any(Review.class));
    }

    @Test
    void testDeleteReviewSuccess() {
        // Arrange
        String id = "1";
        when(reviewRepository.existsById(id)).thenReturn(true);

        // Act
        reviewService.deleteReview(id);

        // Assert
        verify(reviewRepository, times(1)).existsById(id);
        verify(reviewRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteReviewNotFound() {
        // Arrange
        String id = "1";
        when(reviewRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.deleteReview(id);
        });
        assertEquals("Review with id 1 not found", exception.getMessage());
        verify(reviewRepository, times(1)).existsById(id);
        verify(reviewRepository, times(0)).deleteById(anyString());
    }

}
