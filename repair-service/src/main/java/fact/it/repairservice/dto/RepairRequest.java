package fact.it.repairservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepairRequest {
    private List<RepairLineItemDto> repairLineItemsDtoList;
}
