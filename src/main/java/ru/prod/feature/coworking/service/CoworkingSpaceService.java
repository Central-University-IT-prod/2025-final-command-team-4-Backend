package ru.prod.feature.coworking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.prod.common.service.ImageStorageService;
import ru.prod.entity.Account;
import ru.prod.entity.CoworkingSpace;
import ru.prod.entity.CoworkingSpaceFloor;
import ru.prod.feature.account.help.ImageData;
import ru.prod.feature.account.service.AccountService;
import ru.prod.feature.coworking.dto.*;
import ru.prod.feature.coworking.exception.CoworkingNotFoundException;
import ru.prod.feature.coworking.repository.CoworkingFloorRepository;
import ru.prod.feature.coworking.repository.CoworkingRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoworkingSpaceService {

    private final CoworkingRepository coworkingRepository;
    private final CoworkingFloorRepository coworkingFloorRepository;
    private final ImageStorageService imageStorageService;
    private final AccountService accountService;

    public List<CoworkingElementResponse> findAll(LocalDateTime from, LocalDateTime to) {
        return coworkingRepository.findAllElements(from, to).stream().peek(coworkingElementResponse ->
                coworkingElementResponse.setImageUrls(coworkingRepository.findAllImagesElements(coworkingElementResponse.getCoworkingId()))).toList();
    }

    public Optional<CoworkingResponse> findById(UUID coworkingId, LocalDateTime from, LocalDateTime to) {
        Optional<CoworkingResponse> optionalCoworkingResponse = coworkingRepository.findResponseById(coworkingId, from, to);
        if (optionalCoworkingResponse.isEmpty())
            return Optional.empty();

        CoworkingResponse coworkingResponse = optionalCoworkingResponse.get();
        coworkingResponse.setImageUrls(coworkingRepository.findResponseImagesById(coworkingId));

        List<CoworkingFloorResponse> list = new ArrayList<>();
        for (CoworkingFloorResponse coworkingFloorResponse : coworkingRepository.findFloorResponses(coworkingId, from, to)) {

            coworkingFloorResponse.setPlaces(new ArrayList<>());
            for (CoworkingFloorPlaceResponse response: coworkingRepository.findFloorPlaceResponses(coworkingFloorResponse.getFloorId())) {
                response.setBookings(coworkingRepository.findAccountsFloorPlaceResponses(response.getPlaceId(), from, to));
                coworkingFloorResponse.getPlaces().add(response);
            }

            coworkingFloorResponse.setRooms(new ArrayList<>());
            for (CoworkingFloorRoomResponse response: coworkingRepository.findFloorRoomResponses(coworkingFloorResponse.getFloorId())) {
                response.setBookings(coworkingRepository.findAccountsFloorRoomResponses(response.getRoomId(), from, to));
                response.getBookings().addAll(coworkingRepository.findAccountsFloorPlaceResponsesByRoomId(response.getRoomId(), from, to));
                coworkingFloorResponse.getRooms().add(response);
            }

            list.add(coworkingFloorResponse);
        }
        coworkingResponse.setFloors(list);

        return Optional.of(coworkingResponse);
    }

    public void uploadFloorImage(UUID accountId, UUID floorId, MultipartFile file) {
        isAdmin(accountId);

        CoworkingSpaceFloor floor = findFloorById(floorId);

        String imageName = imageStorageService.uploadFile(file);
        floor.setObject(imageName);
        coworkingFloorRepository.save(floor);
    }

    private void isAdmin(UUID accountId) {
        Account account = accountService.findById(accountId);

        if (!account.getIsAdmin()) {
            throw new AccessDeniedException("You don't have permission to get accounts");
        }
    }

    private CoworkingSpaceFloor findFloorById(UUID floorId) {
        return coworkingFloorRepository.findById(floorId)
                .orElseThrow(() -> new CoworkingNotFoundException("Coworking floor with id " + floorId + " not found"));
    }

    public ImageData downloadFloorImage(UUID floorId) {
        CoworkingSpaceFloor floor = findFloorById(floorId);

        if (floor.getObject() == null) {
            return null;
        }

        InputStreamResource resource = imageStorageService.downloadFile(floor.getObject());

        return new ImageData(resource, floor.getObject());
    }
}
