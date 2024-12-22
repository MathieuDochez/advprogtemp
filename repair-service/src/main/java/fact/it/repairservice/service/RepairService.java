package fact.it.repairservice.service;

import fact.it.repairservice.dto.*;
import fact.it.repairservice.model.Repair;
import fact.it.repairservice.model.RepairLineItem;
import fact.it.repairservice.repository.RepairRepository;
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

    @Value("${bikeservice.baseurl}")
    private String bikeServiceBaseUrl;

    @Value("${reviewservice.baseurl}")
    private String reviewServiceBaseUrl;

    public boolean placeRepair(RepairRequest repairRequest) {
        Repair repair = new Repair();
        repair.setRepairNumber(UUID.randomUUID().toString());

        List<RepairLineItem> repairLineItems = repairRequest.getRepairLineItemsDtoList()
                .stream()
                .map(this::mapToRepairLineItem)
                .collect(Collectors.toList());

        repair.setRepairLineItemsList(repairLineItems);

        List<String> skuCodes = repair.getRepairLineItemsList().stream()
                .map(RepairLineItem::getSkuCode)
                .collect(Collectors.toList());

        ReviewResponse[] reviewResponseArray = webClient.get()
                .uri("http://" + reviewServiceBaseUrl + "/api/review",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(ReviewResponse[].class)
                .block();

        BikeResponse[] bikeResponseArray = webClient.get()
                .uri("http://" + bikeServiceBaseUrl + "/api/bike",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(BikeResponse[].class)
                .block();

        repair.getRepairLineItemsList().forEach(repairItem -> {
            BikeResponse bike = Arrays.stream(bikeResponseArray)
                    .filter(p -> p.getSkuCode().equals(repairItem.getSkuCode()))
                    .findFirst()
                    .orElse(null);
            if (bike != null) {
                repairItem.setPrice(bike.getPrice());
            }
        });

        repairRepository.save(repair);
        return true;
    }

    public List<RepairResponse> getAllRepairs() {
        List<Repair> repairs = repairRepository.findAll();

        return repairs.stream()
                .map(repair -> new RepairResponse(
                        repair.getRepairNumber(),
                        mapToRepairLineItemsDto(repair.getRepairLineItemsList())
                ))
                .collect(Collectors.toList());
    }

    public void updateRepair(Long id, RepairRequest repairRequest) {
        Repair repair = repairRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repair with id " + id + " not found"));

        List<RepairLineItem> repairLineItems = repairRequest.getRepairLineItemsDtoList()
                .stream()
                .map(this::mapToRepairLineItem)
                .collect(Collectors.toList());

        repair.setRepairLineItemsList(repairLineItems);

        repairRepository.save(repair);
    }

    public void deleteRepair(Long id) {
        if (repairRepository.existsById(id)) {
            repairRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Repair with id " + id + " not found");
        }
    }

    private RepairLineItem mapToRepairLineItem(RepairLineItemDto repairLineItemDto) {
        RepairLineItem repairLineItem = new RepairLineItem();
        repairLineItem.setPrice(repairLineItemDto.getPrice());
        repairLineItem.setQuantity(repairLineItemDto.getQuantity());
        repairLineItem.setSkuCode(repairLineItemDto.getSkuCode());
        return repairLineItem;
    }

    private List<RepairLineItemDto> mapToRepairLineItemsDto(List<RepairLineItem> repairLineItems) {
        return repairLineItems.stream()
                .map(repairLineItem -> new RepairLineItemDto(
                        repairLineItem.getId(),
                        repairLineItem.getSkuCode(),
                        repairLineItem.getPrice(),
                        repairLineItem.getQuantity()
                ))
                .collect(Collectors.toList());
    }
}
