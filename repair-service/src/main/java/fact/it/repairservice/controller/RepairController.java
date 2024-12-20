package fact.it.repairservice.controller;

import fact.it.repairservice.dto.RepairRequest;
import fact.it.repairservice.dto.RepairResponse;
import fact.it.repairservice.model.Repair;
import fact.it.repairservice.service.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repair")
@RequiredArgsConstructor
public class RepairController {

    private final RepairService repairService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String placeRepair(@RequestBody RepairRequest repairRequest) {
        boolean result = repairService.placeRepair(repairRequest);
        return (result ? "Repair request made successfully" : "Repair request failed");
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RepairResponse> getAllRepairs() {
        return repairService.getAllRepairs();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateRepair(@PathVariable Long id, @RequestBody RepairRequest repairRequest) {
        repairService.updateRepair(id, repairRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteRepair(@PathVariable Long id) {
        repairService.deleteRepair(id);
    }
}
