package fact.it.bikeservice.service;

import fact.it.bikeservice.dto.BikeRequest;
import fact.it.bikeservice.dto.BikeResponse;
import fact.it.bikeservice.model.Bike;
import fact.it.bikeservice.repository.BikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BikeService {

    private final BikeRepository bikeRepository;

    public void createBike(BikeRequest bikeRequest) {
        Bike bike = Bike.builder()
                .skuCode(bikeRequest.getSkuCode())
                .name(bikeRequest.getName())
                .description(bikeRequest.getDescription())
                .price(bikeRequest.getPrice())
                .build();

        bikeRepository.save(bike);
    }

    public List<BikeResponse> getAllBikes() {
        List<Bike> bikes = bikeRepository.findAll();

        return bikes.stream().map(this::mapToBikeResponse).toList();
    }

    public List<BikeResponse> getAllBikesBySkuCode(List<String> skuCode) {
        List<Bike> bikes = bikeRepository.findBySkuCodeIn(skuCode);

        return bikes.stream().map(this::mapToBikeResponse).toList();
    }

    public void updateBike(String id, BikeRequest bikeRequest) {
        Optional<Bike> optionalBike = bikeRepository.findById(id);

        if (optionalBike.isPresent()) {
            Bike bike = optionalBike.get();
            bike.setSkuCode(bikeRequest.getSkuCode());
            bike.setName(bikeRequest.getName());
            bike.setDescription(bikeRequest.getDescription());
            bike.setPrice(bikeRequest.getPrice());

            bikeRepository.save(bike);
        } else {
            throw new IllegalArgumentException("Bike with id " + id + " not found");
        }
    }

    public void deleteBike(String id) {
        if (bikeRepository.existsById(id)) {
            bikeRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Bike with id " + id + " not found");
        }
    }

    private BikeResponse mapToBikeResponse(Bike bike) {
        return BikeResponse.builder()
                .id(bike.getId())
                .skuCode(bike.getSkuCode())
                .name(bike.getName())
                .description(bike.getDescription())
                .price(bike.getPrice())
                .build();
    }

}
