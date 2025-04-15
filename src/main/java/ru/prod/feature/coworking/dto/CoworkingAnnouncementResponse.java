package ru.prod.feature.coworking.dto;

import lombok.Data;
import ru.prod.feature.account.dto.AccountProfileResponse;

import java.time.LocalDateTime;

@Data
public class CoworkingAnnouncementResponse {

    private String title;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private CoworkingResponse coworking;
}
