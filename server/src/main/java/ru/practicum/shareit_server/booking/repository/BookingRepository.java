package ru.practicum.shareit_server.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit_server.booking.model.Booking;
import ru.practicum.shareit_server.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    @Query(" SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= :time " +
            "AND b.end >= :time " +
            "ORDER BY b.start DESC")
    Page<Booking> findByCurrentTime(Long bookerId, LocalDateTime time, Pageable pageable);

    Page<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime time, Pageable pageable);

    Page<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime time, Pageable pageable);

    Page<Booking> findAllByItemIdIn(List<Long> itemIds, Pageable pageable);

    List<Booking> findAllByItemIdIn(List<Long> itemIds, Sort sort);

    @Query(" SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.start <= :time " +
            "AND b.end >= :time " +
            "AND b.item.id IN (:itemIds) " +
            "ORDER BY b.start DESC")
    Page<Booking> findByCurrentTime(LocalDateTime time, List<Long> itemIds, Pageable pageable);

    Page<Booking> findByStatusAndItemIdIn(BookingStatus status, List<Long> itemIds, Pageable pageable);

    Page<Booking> findByEndBeforeAndItemIdIn(LocalDateTime time, List<Long> itemIds, Pageable pageable);

    Page<Booking> findByStartAfterAndItemIdIn(LocalDateTime time, List<Long> itemIds, Pageable pageable);

    List<Booking> findAllByItemId(Long itemId, Sort sort);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusAndEndBefore(Long userId,
                                                                        Long itemId,
                                                                        BookingStatus status,
                                                                        LocalDateTime end);
}
