package com.example.jdbc.project.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Seat {

    private Integer aircraftId;
    private String seatNo;
}
