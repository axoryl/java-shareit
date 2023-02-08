package ru.practicum.shareit_gateway.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit_gateway.booking.BookingStatus;
import ru.practicum.shareit_gateway.item.dto.ItemShortDto;
import ru.practicum.shareit_gateway.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {

    private Long id;
    private BookingStatus status;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserShortDto booker;
    private ItemShortDto item;
}
