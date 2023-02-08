package ru.practicum.shareit_gateway.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit_gateway.item.client.ItemClient;
import ru.practicum.shareit_gateway.item.dto.CommentCreationDto;
import ru.practicum.shareit_gateway.item.dto.CommentInfoDto;
import ru.practicum.shareit_gateway.item.dto.ItemCreationDto;
import ru.practicum.shareit_gateway.item.dto.ItemInfoDto;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    private ItemClient itemClient;

    @SneakyThrows
    @Test
    void findItemById_thenResponseIsOk() {
        final var item = getItemInfoDto();
        when(itemClient.findById(1L, 1L)).thenReturn(ResponseEntity.ok().body(item));

        final var result = mockMvc.perform(get("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(item), result),
                () -> verify(itemClient).findById(1L, 1L)
        );
    }

    @SneakyThrows
    @Test
    void findAllOwnerItems_thenResponseIsOk() {
        final var items = List.of(getItemInfoDto());
        when(itemClient.findAllOwnerItems(1L, 0, 10)).thenReturn(ResponseEntity.ok().body(items));

        mockMvc.perform(get("/items?from={from}&size={size}", 0, 10)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(itemClient).findAllOwnerItems(1L, 0, 10);
    }

    @SneakyThrows
    @Test
    void search_thenResponseIsOk() {
        final var items = List.of(getItemCreationDto());
        when(itemClient.search("text", 0, 10)).thenReturn(ResponseEntity.ok().body(items));

        mockMvc.perform(get("/items/search?text={text}&from={from}&size={size}", "text", 0, 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(itemClient).search("text", 0, 10);
    }

    @SneakyThrows
    @Test
    void saveValidItem_thenResponseIsOk() {
        final var item = getItemCreationDto();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        when(itemClient.save(1L, item)).thenReturn(ResponseEntity.ok().body(item));

        final var result = mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(item), result),
                () -> verify(itemClient).save(1L, item)
        );
    }

    @SneakyThrows
    @Test
    void saveNotValidItem_nameIsBlank_thenResponseIsBadRequest() {
        final var item = getItemCreationDto();
        item.setName("");
        item.setDescription("description");
        item.setAvailable(true);

        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).save(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void saveNotValidItem_descriptionIsBlank_thenResponseIsBadRequest() {
        final var item = getItemCreationDto();
        item.setName("name");
        item.setDescription("");
        item.setAvailable(true);

        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).save(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void saveNotValidItem_availableIsNull_thenResponseIsBadRequest() {
        final var item = getItemCreationDto();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(null);

        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).save(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void addValidComment_thenResponseIsOk() {
        final var createdComment = getCommentCreationDto();
        createdComment.setText("text");
        final var comment = getCommentInfoDto();
        when(itemClient.addComment(1L, 1L, createdComment)).thenReturn(ResponseEntity.ok().body(comment));

        final var result = mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createdComment)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(comment), result),
                () -> verify(itemClient).addComment(1L, 1L, createdComment)
        );
    }

    @SneakyThrows
    @Test
    void addNotValidComment_textIsBlank_thenResponseIsBadRequest() {
        final var createdComment = getCommentCreationDto();
        createdComment.setText("");

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createdComment)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any());
    }

    @SneakyThrows
    @Test
    void updateValidItem_thenResponseIsOk() {
        final var itemToUpdate = getItemCreationDto();
        itemToUpdate.setName("new name");
        itemToUpdate.setDescription("new desc");
        itemToUpdate.setAvailable(true);

        when(itemClient.update(1L, 1L, itemToUpdate)).thenReturn(ResponseEntity.ok().body(itemToUpdate));

        final var result = mockMvc.perform(patch("/items/{id}", 1L)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemToUpdate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(itemToUpdate), result),
                () -> verify(itemClient).update(1L, 1L, itemToUpdate)
        );
    }

    private ItemInfoDto getItemInfoDto() {
        return ItemInfoDto.builder().build();
    }

    private ItemCreationDto getItemCreationDto() {
        return ItemCreationDto.builder().build();
    }

    private CommentCreationDto getCommentCreationDto() {
        return CommentCreationDto.builder().build();
    }

    private CommentInfoDto getCommentInfoDto() {
        return CommentInfoDto.builder().build();
    }
}
