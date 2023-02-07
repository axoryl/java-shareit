package ru.practicum.shareit_gateway.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit_gateway.booking.dto.BookingShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemInfoDto {

    private Long id;

    @NotNull
    private Long ownerId;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentInfoDto> comments;
}
