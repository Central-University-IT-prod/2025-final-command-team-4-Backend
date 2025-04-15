package ru.prod.feature.coworking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.prod.entity.CoworkingSpaceFloor;

import java.util.UUID;

public interface CoworkingFloorRepository extends JpaRepository<CoworkingSpaceFloor, UUID> {

}
