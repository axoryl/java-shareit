package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Data
@Jacksonized
@Builder
public class ItemRequestCreationDto {

    @NotBlank
    private String description;
}
