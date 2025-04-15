package ru.prod.feature.coworking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.prod.feature.account.help.ImageData;
import ru.prod.feature.coworking.api.CoworkingSpaceApi;
import ru.prod.feature.coworking.dto.CoworkingElementResponse;
import ru.prod.feature.coworking.dto.CoworkingResponse;
import ru.prod.feature.coworking.service.CoworkingSpaceService;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CoworkingSpaceController implements CoworkingSpaceApi {

    private final CoworkingSpaceService coworkingSpaceService;

    @Override
    public List<CoworkingElementResponse> findAll() {
        return coworkingSpaceService.findAll(
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Override
    public CoworkingResponse findCoworkingSpace(UUID coworkingId, ZonedDateTime startAt, ZonedDateTime endAt) {
        return coworkingSpaceService.findById(coworkingId, startAt.toLocalDateTime(), endAt.toLocalDateTime())
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void uploadFloorImage(UUID accountId, MultipartFile file, UUID floorId) {
        coworkingSpaceService.uploadFloorImage(accountId, floorId, file);
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadFloorImage(UUID floorId) {
        ImageData imageData = coworkingSpaceService.downloadFloorImage(floorId);

        if (imageData == null) {
            return ResponseEntity.noContent().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + imageData.filename());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(imageData.resource());
    }
}
