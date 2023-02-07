package ru.practicum.shareit_gateway.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit_gateway.booking.BookingState;
import ru.practicum.shareit_gateway.booking.dto.BookingCreationDto;
import ru.practicum.shareit_gateway.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findById(final Long userId, final Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findAllByState(final Long userId, final BookingState state,
                                                 final Integer from, final Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findAllByStateForOwner(final Long userId, final BookingState state,
                                                         final Integer from, final Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> save(final Long userId, final BookingCreationDto booking) {
        return post("", userId, booking);
    }

    public ResponseEntity<Object> approve(final Long ownerId, final Long bookingId, final Boolean isApprove) {
        Map<String, Object> parameters = Map.of(
                "approved", isApprove
        );
        return patch("/" + bookingId + "?approved={approved}", ownerId, parameters, null);
    }
}
