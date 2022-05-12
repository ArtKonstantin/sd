package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TicketUpdateAcceptedResponceDTO {
    private long id;
    private long appointed_id;
    private String status;
    private String description;
    private String file;
}
