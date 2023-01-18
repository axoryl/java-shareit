package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto mapToBookingDto(final Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .status(booking.getStatus())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(UserMapper.mapToUserShortDto(booking.getBooker()))
                .item(ItemMapper.mapToItemShortDto(booking.getItem()))
                .build();
    }

    public static BookingShortDto mapToBookingShortDto(final Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Booking mapToBooking(final BookingCreationDto bookingCreationDto,
                                       final User user,
                                       final Item item) {
        return Booking.builder()
                .booker(user)
                .item(item)
                .start(bookingCreationDto.getStart())
                .end(bookingCreationDto.getEnd())
                .status(BookingStatus.WAITING)
                .build();
    }
}
