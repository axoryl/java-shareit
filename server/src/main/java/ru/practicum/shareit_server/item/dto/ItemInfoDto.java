package ru.practicum.shareit_server.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit_server.booking.dto.BookingShortDto;

import java.util.List;

@Data
@Builder
public class ItemInfoDto {

    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentInfoDto> comments;
}
