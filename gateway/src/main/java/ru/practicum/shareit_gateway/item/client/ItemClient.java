package ru.practicum.shareit_gateway.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit_gateway.client.BaseClient;
import ru.practicum.shareit_gateway.item.dto.CommentCreationDto;
import ru.practicum.shareit_gateway.item.dto.ItemCreationDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findById(final Long userId, final Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> findAllOwnerItems(final Long ownerId, final Integer from, final Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> search(final String text, final Integer from, final Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> save(final Long userId, final ItemCreationDto item) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> addComment(final Long userId, final Long itemId, final CommentCreationDto comment) {
        return post("/" + itemId + "/comment", userId, comment);
    }

    public ResponseEntity<Object> update(final Long itemId, final Long ownerId, final ItemCreationDto item) {
        return patch("/" + itemId, ownerId, item);
    }
}
