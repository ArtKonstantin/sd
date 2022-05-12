package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TicketUpdateArchivedByIdResponceDTO {
    private long id;
    private String status;
    private String description;
    private String file;
}
