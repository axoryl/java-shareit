package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    @Query(" SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= :time " +
            "AND b.end >= :time " +
            "ORDER BY b.start DESC")
    List<Booking> findByCurrentTime(Long bookerId, LocalDateTime time);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime time, Sort sort);

    List<Booking> findAllByItemIdIn(List<Long> itemIds, Sort sort);

    @Query(" SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.start <= :time " +
            "AND b.end >= :time " +
            "AND b.item.id IN (:itemIds) " +
            "ORDER BY b.start DESC")
    List<Booking> findByCurrentTime(LocalDateTime time, List<Long> itemIds);

    List<Booking> findByStatusAndItemIdIn(BookingStatus status, List<Long> itemIds, Sort sort);

    List<Booking> findByEndBeforeAndItemIdIn(LocalDateTime time, List<Long> itemIds, Sort sort);

    List<Booking> findByStartAfterAndItemIdIn(LocalDateTime time, List<Long> itemIds, Sort sort);

    List<Booking> findAllByItemId(Long itemId, Sort sort);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusAndEndBefore(Long userId,
                                                                        Long itemId,
                                                                        BookingStatus status,
                                                                        LocalDateTime end);
}
