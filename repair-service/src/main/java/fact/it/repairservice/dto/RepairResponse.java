package fact.it.repairservice.dto;

import fact.it.repairservice.dto.RepairLineItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepairResponse {
    private String repairNumber;
    private List<RepairLineItemDto> repairLineItemsList;
}
