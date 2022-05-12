package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketSelectByTypeSlaResponceDTO {
    private String criticality;
    private String status;
    private String customer;
    private String description;
    private String file;
    private String appointed;
    private String solution;
}
