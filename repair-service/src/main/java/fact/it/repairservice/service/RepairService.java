package fact.it.repairservice.service;

import fact.it.repairservice.dto.*;
import fact.it.repairservice.model.Repair;
import fact.it.repairservice.model.RepairLineItem;
import fact.it.repairservice.repository.RepairRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    public boolean placeRepair(RepairRequest repairRequest) {
        Repair repair = new Repair();
        repair.setRepairNumber(UUID.randomUUID().toString());

        List<RepairLineItem> repairLineItems = repairRequest.getRepairLineItemsDtoList()
                .stream()
                .map(this::mapToRepairLineItem)
                .toList();

        repair.setRepairLineItemsList(repairLineItems);

        List<String> skuCodes = repair.getRepairLineItemsList().stream()
                .map(RepairLineItem::getSkuCode)
                .toList();

        ReviewResponse[] reviewResponseArray = webClient.get()
                .uri("http://localhost:8082/api/repair",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(ReviewResponse[].class)
                .block();

        BikeResponse[] bikeResponseArray = webClient.get()
                .uri("http://localhost:8080/api/bike",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(BikeResponse[].class)
                .block();

        repair.getRepairLineItemsList().stream()
                .map(repairItem -> {
                    BikeResponse bike = Arrays.stream(bikeResponseArray)
                            .filter(p -> p.getSkuCode().equals(repairItem.getSkuCode()))
                            .findFirst()
                            .orElse(null);
                    if (bike != null) {
                        repairItem.setPrice(bike.getPrice());
                    }
                    return repairItem;
                })
                .collect(Collectors.toList());

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
        Optional<Repair> optionalRepair = repairRepository.findById(id);

        if (optionalRepair.isPresent()) {
            Repair repair = optionalRepair.get();
            List<RepairLineItem> repairLineItems = repairRequest.getRepairLineItemsDtoList()
                    .stream()
                    .map(this::mapToRepairLineItem)
                    .toList();

            repair.setRepairLineItemsList(repairLineItems);

            repairRepository.save(repair);
        } else {
            throw new IllegalArgumentException("Repair with id " + id + " not found");
        }
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
