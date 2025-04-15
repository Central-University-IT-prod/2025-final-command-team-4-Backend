package ru.prod.feature.coworking.mapper;

import org.mapstruct.Mapper;
import ru.prod.entity.CoworkingAnnouncement;
import ru.prod.feature.coworking.dto.CoworkingAnnouncementResponse;

@Mapper(componentModel = "spring")
public interface CoworkingAnnouncementMapper {

    CoworkingAnnouncementResponse toResponse(CoworkingAnnouncement announcement);
}
