package ru.prod.feature.coworking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.prod.entity.CoworkingAnnouncement;

import java.util.UUID;

@Repository
public interface CoworkingAnnouncementRepository extends JpaRepository<CoworkingAnnouncement, UUID> {

}
