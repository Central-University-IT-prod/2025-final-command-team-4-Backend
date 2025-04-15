package ru.prod.feature.coworking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.prod.feature.coworking.api.CoworkingAnnouncementApi;
import ru.prod.feature.coworking.dto.CoworkingAnnouncementRequest;
import ru.prod.feature.coworking.dto.CoworkingAnnouncementResponse;
import ru.prod.feature.coworking.service.CoworkingAnnouncementService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CoworkingAnnouncementController implements CoworkingAnnouncementApi {

    private final CoworkingAnnouncementService coworkingAnnouncementService;

    @Override
    public CoworkingAnnouncementResponse create(UUID accountId, CoworkingAnnouncementRequest request) {
        return coworkingAnnouncementService.create(accountId, request);
    }

    @Override
    public List<CoworkingAnnouncementResponse> getAnnouncements(Integer page, Integer size) {
        return coworkingAnnouncementService.getAnnouncements(page, size);
    }
}
