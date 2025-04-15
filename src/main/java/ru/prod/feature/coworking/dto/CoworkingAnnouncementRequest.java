package ru.prod.feature.coworking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CoworkingAnnouncementRequest {
    @NotBlank(message = "Announcement title is required")
    private String title;

    @NotBlank(message = "Announcement content is required")
    private String content;

    @NotNull(message = "room_book_id is required")
    private UUID roomBookId;
}
