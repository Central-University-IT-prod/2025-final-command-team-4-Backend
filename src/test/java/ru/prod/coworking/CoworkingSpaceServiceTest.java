package ru.prod.coworking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.prod.feature.coworking.dto.*;
import ru.prod.feature.coworking.repository.CoworkingRepository;
import ru.prod.feature.coworking.service.CoworkingSpaceService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoworkingSpaceServiceTest {
    @Mock
    private CoworkingRepository coworkingRepository;

    @InjectMocks
    private CoworkingSpaceService coworkingService;

    private UUID coworkingId;
    private LocalDateTime from;
    private LocalDateTime to;

    @BeforeEach
    void setUp() {
        coworkingId = UUID.randomUUID();
        from = LocalDateTime.now();
        to = LocalDateTime.now().plusHours(1);
    }

    @Test
    void findAll_Success() {
        var from = LocalDateTime.now();
        var to = from.plusHours(2);

        var coworkingId = UUID.randomUUID();

        var response = new CoworkingElementResponse(coworkingId, "title", "Location", "Description", 5, null);
        when(coworkingRepository.findAllElements(from, to)).thenReturn(List.of(response));
        when(coworkingRepository.findAllImagesElements(coworkingId)).thenReturn(List.of("image1.jpg", "image2.jpg"));

        List<CoworkingElementResponse> result = coworkingService.findAll(from, to);

        assertEquals(1, result.size());
        assertEquals(coworkingId, result.getFirst().getCoworkingId());
        assertEquals("Location", result.getFirst().getLocation());
        assertEquals(List.of("image1.jpg", "image2.jpg"), result.getFirst().getImageUrls());
    }

    @Test
    void findById_WhenCoworkingResponseNotFound_ShouldReturnEmptyOptional() {
        when(coworkingRepository.findResponseById(coworkingId, from, to)).thenReturn(Optional.empty());

        Optional<CoworkingResponse> result = coworkingService.findById(coworkingId, from, to);

        assertTrue(result.isEmpty());
        verify(coworkingRepository, times(1)).findResponseById(coworkingId, from, to);
        verifyNoMoreInteractions(coworkingRepository);
    }

    @Test
    void findById_WhenCoworkingResponseFound_ShouldReturnPopulatedOptional() {
        CoworkingResponse coworkingResponse = new CoworkingResponse();

        when(coworkingRepository.findResponseById(coworkingId, from, to)).thenReturn(Optional.of(coworkingResponse));
        when(coworkingRepository.findResponseImagesById(coworkingId)).thenReturn(Arrays.asList("image1.jpg", "image2.jpg"));

        UUID floorId = UUID.randomUUID();
        var floorResponse = new CoworkingFloorResponse();
        floorResponse.setFloorId(floorId);

        when(coworkingRepository.findFloorResponses(coworkingId, from, to)).thenReturn(List.of(floorResponse));
        when(coworkingRepository.findFloorPlaceResponses(floorId)).thenReturn(List.of(new CoworkingFloorPlaceResponse()));
        when(coworkingRepository.findFloorRoomResponses(floorId)).thenReturn(List.of(new CoworkingFloorRoomResponse()));

        Optional<CoworkingResponse> result = coworkingService.findById(coworkingId, from, to);

        assertTrue(result.isPresent());
        CoworkingResponse response = result.get();
        assertEquals(2, response.getImageUrls().size());
        assertEquals(1, response.getFloors().size());
        assertEquals(1, response.getFloors().getFirst().getPlaces().size());
        assertEquals(1, response.getFloors().getFirst().getRooms().size());

        verify(coworkingRepository, times(1)).findResponseById(coworkingId, from, to);
        verify(coworkingRepository, times(1)).findResponseImagesById(coworkingId);
        verify(coworkingRepository, times(1)).findFloorResponses(coworkingId, from, to);
        verify(coworkingRepository, times(1)).findFloorPlaceResponses(floorId);
        verify(coworkingRepository, times(1)).findFloorRoomResponses(floorId);
    }
}
