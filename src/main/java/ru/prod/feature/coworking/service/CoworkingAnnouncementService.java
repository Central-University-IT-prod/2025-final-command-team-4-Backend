package ru.prod.feature.coworking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.prod.entity.Account;
import ru.prod.entity.CoworkingAnnouncement;
import ru.prod.entity.CoworkingSpaceFloorRoomBook;
import ru.prod.feature.account.service.AccountService;
import ru.prod.feature.coworking.dto.CoworkingAnnouncementRequest;
import ru.prod.feature.coworking.dto.CoworkingAnnouncementResponse;
import ru.prod.feature.coworking.mapper.CoworkingAnnouncementMapper;
import ru.prod.feature.coworking.repository.CoworkingAnnouncementRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoworkingAnnouncementService {
    private final CoworkingAnnouncementRepository coworkingAnnouncementRepository;
    private final CoworkingBookService coworkingBookService;
    private final AccountService accountService;
    private final CoworkingAnnouncementMapper coworkingAnnouncementMapper;

    public CoworkingAnnouncementResponse create(UUID accountId, CoworkingAnnouncementRequest request) {
        Account account = accountService.findById(accountId);
        CoworkingSpaceFloorRoomBook roomBook = coworkingBookService.findRoomBookById(request.getRoomBookId());

        if (!roomBook.getBookingAccount().getId().equals(account.getId())) {
            throw new AccessDeniedException("You do not have permission to book this room");
        }

        var announcement = CoworkingAnnouncement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .startTime(roomBook.getStartTime())
                .endTime(roomBook.getEndTime())
                .account(account)
                .roomBook(roomBook)
                .build();

        announcement = coworkingAnnouncementRepository.save(announcement);
        return coworkingAnnouncementMapper.toResponse(announcement);
    }

    public List<CoworkingAnnouncementResponse> getAnnouncements(int page, int size) {
        return coworkingAnnouncementRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime")))
                .stream()
                .map(coworkingAnnouncementMapper::toResponse)
                .toList();
    }
}
