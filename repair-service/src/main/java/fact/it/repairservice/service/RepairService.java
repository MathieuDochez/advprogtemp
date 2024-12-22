package fact.it.repairservice.service;

import fact.it.repairservice.dto.*;
import fact.it.repairservice.model.Repair;
import fact.it.repairservice.model.RepairLineItem;
import fact.it.repairservice.repository.RepairRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RepairService {

    private final RepairRepository repairRepository;
    private final WebClient webClient;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${bikeservice.baseurl}")
    private String bikeServiceBaseUrl;

    @Value("${reviewservice.baseurl}")
    private String reviewServiceBaseUrl;

    @Transactional
    public void placeRepair(RepairRequest repairRequest) {
        Repair repair = new Repair();
        repair.setRepairNumber(UUID.randomUUID().toString());

        BikeResponse bike = webClient.get()
                .uri("http://" + bikeServiceBaseUrl + "/api/bike",
                        uriBuilder -> uriBuilder.queryParam("skuCode", repairRequest.getBikeSkuCode()).build())
                .retrieve()
                .bodyToMono(BikeResponse.class)
                .block();

        ReviewResponse review = webClient.get()
                .uri("http://" + reviewServiceBaseUrl + "/api/review",
                        uriBuilder -> uriBuilder.queryParam("id", repairRequest.getReviewId()).build())
                .retrieve()
                .bodyToMono(ReviewResponse.class)
                .block();

        assert bike != null;
        assert review != null;

        repair.setBike(mapBikeResponseToEntity(bike));
        repair.setReview(mapReviewResponseToEntity(review));

        repairRepository.save(repair);
    }

    private Bike mapBikeResponseToEntity(BikeResponse bikeResponse) {
        Bike bike = new Bike();
        bike.setSkuCode(bikeResponse.getSkuCode());
        bike.setName(bikeResponse.getName());
        bike.setDescription(bikeResponse.getDescription());
        bike.setPrice(bikeResponse.getPrice());
        return bike;
    }

    private Review mapReviewResponseToEntity(ReviewResponse reviewResponse) {
        Review review = new Review();
        review.setId(reviewResponse.getId());
        review.setComment(reviewResponse.getComment());
        review.setRating(reviewResponse.getRating());
        return review;
    }

    public List<RepairResponse> getAllRepairs() {
        List<Repair> repairs = repairRepository.findAll();

        return repairs.stream()
                .map(repair -> new RepairResponse(
                        repair.getRepairNumber(),
                        repair.getBike(),
                        repair.getReview()
                ))
                .collect(Collectors.toList());
    }
}
