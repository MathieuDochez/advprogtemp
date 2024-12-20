package fact.it.bikeservice.controller;

import fact.it.bikeservice.dto.BikeRequest;
import fact.it.bikeservice.dto.BikeResponse;
import fact.it.bikeservice.service.BikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bike")
@RequiredArgsConstructor
public class BikeController {

    private final BikeService bikeService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createBike
            (@RequestBody BikeRequest bikeRequest) {
        bikeService.createBike(bikeRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BikeResponse> getAllBikesBySkuCode
            (@RequestParam List<String> skuCode) {
        return bikeService.getAllBikesBySkuCode(skuCode);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<BikeResponse> getAllBikes() {
        return bikeService.getAllBikes();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateBike(@PathVariable String id, @RequestBody BikeRequest bikeRequest) {
        bikeService.updateBike(id, bikeRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBike(@PathVariable String id) {
        bikeService.deleteBike(id);
    }
}

