package com.example.jdbc.project.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TicketFilter {
    int limit;
    int offset;
}
