package ru.prod.feature.coworking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.prod.entity.CoworkingSpaceFloorPlaceBook;

import java.util.UUID;

@Repository
public interface CoworkingPlaceBookRepository extends JpaRepository<CoworkingSpaceFloorPlaceBook, UUID> {
}
