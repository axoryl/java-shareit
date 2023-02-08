package ru.practicum.shareit_server.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class ItemRequestCreationDto {

    private String description;
}
