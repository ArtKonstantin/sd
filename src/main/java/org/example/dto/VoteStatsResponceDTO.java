package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VoteStatsResponceDTO {
    private String appointed;
    private int minRating;
    private int maxRating;
    private double avgRating;
}
