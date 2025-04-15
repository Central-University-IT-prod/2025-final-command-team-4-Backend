package ru.prod.feature.coworking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.prod.entity.CoworkingSpace;
import ru.prod.feature.coworking.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CoworkingRepository extends JpaRepository<CoworkingSpace, UUID> {

    @Query("""
    SELECT new ru.prod.feature.coworking.dto.CoworkingElementResponse(
        cs.id,
        cs.title,
        cs.location,
        cs.description,
        cast((
            SELECT COUNT (DISTINCT pl)
            FROM CoworkingSpaceFloorPlace pl
            LEFT JOIN CoworkingSpaceFloorPlaceBook pl_book ON pl.id = pl_book.coworkingSpaceFloorPlace.id
            LEFT JOIN CoworkingSpaceFloorRoom pl_room ON pl.coworkingSpaceFloorRoom is not null AND pl.coworkingSpaceFloorRoom.id = pl_room.id
            LEFT JOIN CoworkingSpaceFloorRoomBook pl_room_book ON pl_room is not null AND pl_room.id = pl_room_book.coworkingSpaceFloorRoom.id
            WHERE
                (pl_book is null OR :startTime <= pl_book.startTime OR :endTime >= pl_book.endTime) AND
                (pl_room_book is null OR :startTime <= pl_room_book.startTime OR :endTime >= pl_room_book.endTime)
        ) as integer),
        null
    )
    FROM CoworkingSpace cs
    ORDER BY cs.location DESC
    """)
    List<CoworkingElementResponse> findAllElements(@Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    @Query("""
    SELECT csi.object
    FROM CoworkingSpaceImage csi
    WHERE csi.coworkingSpace.id = :coworkingId
    ORDER BY csi.imageOrder ASC
    """)
    List<String> findAllImagesElements(@Param("coworkingId") UUID coworkingId);

    @Query("""
    SELECT new ru.prod.feature.coworking.dto.CoworkingResponse(
        cs.location,
        cs.title,
        cs.time,
        cs.description,
        null,
        null,
        CAST ((
            SELECT COUNT (DISTINCT pl)
            FROM CoworkingSpaceFloorPlace pl
            LEFT JOIN CoworkingSpaceFloorPlaceBook pl_book ON pl.id = pl_book.coworkingSpaceFloorPlace.id
            LEFT JOIN CoworkingSpaceFloorRoom pl_room ON pl.coworkingSpaceFloorRoom is not null AND pl.coworkingSpaceFloorRoom.id = pl_room.id
            LEFT JOIN CoworkingSpaceFloorRoomBook pl_room_book ON pl_room is not null AND pl_room.id = pl_room_book.coworkingSpaceFloorRoom.id
            WHERE
                (pl_book is null OR :startTime <= pl_book.startTime OR :endTime >= pl_book.endTime) AND
                (pl_room_book is null OR :startTime <= pl_room_book.startTime OR :endTime >= pl_room_book.endTime)
        ) as integer)
    )
    FROM CoworkingSpace cs
    WHERE cs.id = :coworkingId
    """)
    Optional<CoworkingResponse> findResponseById(@Param("coworkingId") UUID coworkingId,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    @Query("""
    SELECT new list(csi.object)
    FROM CoworkingSpaceImage csi
    WHERE csi.coworkingSpace.id = :coworkingId
    ORDER BY csi.imageOrder ASC
    """)
    List<String> findResponseImagesById(@Param("coworkingId") UUID coworkingId);

    @Query("""
    SELECT new ru.prod.feature.coworking.dto.CoworkingFloorResponse(
        csf.id,
        null,
        null,
        cast((
            SELECT COUNT (DISTINCT plf)
            FROM CoworkingSpaceFloorPlace plf
            LEFT JOIN CoworkingSpaceFloorPlaceBook pl_book ON plf.id = pl_book.coworkingSpaceFloorPlace.id
            LEFT JOIN CoworkingSpaceFloorRoom pl_room ON plf.coworkingSpaceFloorRoom is not null AND plf.coworkingSpaceFloorRoom.id = pl_room.id
            LEFT JOIN CoworkingSpaceFloorRoomBook pl_room_book ON pl_room is not null AND pl_room.id = pl_room_book.coworkingSpaceFloorRoom.id
            WHERE
                plf.coworkingSpaceFloor.id = csf.id AND
                (pl_book is null OR :startTime <= pl_book.startTime OR :endTime >= pl_book.endTime) AND
                (pl_room_book is null OR :startTime <= pl_room_book.startTime OR :endTime >= pl_room_book.endTime)
        ) as integer),
        csf.floorOrder
    )
    FROM CoworkingSpaceFloor csf
    WHERE csf.coworkingSpace.id = :coworkingId
    ORDER BY csf.floorOrder ASC
    """)
    List<CoworkingFloorResponse> findFloorResponses(@Param("coworkingId") UUID coworkingId,
                                                    @Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime);

    @Query("""
    SELECT new ru.prod.feature.coworking.dto.CoworkingFloorPlaceResponse(
        pl.id,
        pl.latitude,
        pl.longitude,
        pl.price,
        null
    )
    FROM CoworkingSpaceFloorPlace pl
    WHERE pl.coworkingSpaceFloor.id = :floorId
    ORDER BY pl.id
    """)
    List<CoworkingFloorPlaceResponse> findFloorPlaceResponses(@Param("floorId") UUID floorId);

    @Query("""
    SELECT new ru.prod.feature.coworking.dto.CoworkingAccountBookingResponse(
        csfp.bookingAccount.login,
        csfp.startTime,
        csfp.endTime
    )
    FROM CoworkingSpaceFloorPlaceBook csfp
    WHERE csfp.coworkingSpaceFloorPlace.id = :placeId AND (
        (:startTime <= csfp.startTime AND csfp.startTime <= :endTime AND :endTime <= csfp.endTime) OR
        (:startTime >= csfp.startTime AND :startTime <= csfp.endTime AND :endTime >= csfp.startTime AND :endTime <= csfp.endTime) OR
        (:startTime >= csfp.startTime AND :startTime <= csfp.endTime AND :endTime <= csfp.endTime) OR
        (:endTime >= csfp.endTime AND :startTime <= csfp.startTime)
    ) AND csfp.exitTime is null
    """)
    List<CoworkingAccountBookingResponse> findAccountsFloorPlaceResponses(@Param("placeId") UUID placeId,
                                                                          @Param("startTime") LocalDateTime startTime,
                                                                          @Param("endTime") LocalDateTime endTime);

    @Query("""
    SELECT new ru.prod.feature.coworking.dto.CoworkingFloorRoomResponse(
        rm.id,
        rm.latitude,
        rm.longitude,
        rm.price,
        null
    )
    FROM CoworkingSpaceFloorRoom rm
    WHERE rm.coworkingSpaceFloor.id = :floorId
    ORDER BY rm.id
    """)
    List<CoworkingFloorRoomResponse> findFloorRoomResponses(@Param("floorId") UUID floorId);

    @Query("""
    SELECT new ru.prod.feature.coworking.dto.CoworkingAccountBookingResponse(
        csfp.bookingAccount.login,
        csfp.startTime,
        csfp.endTime
    )
    FROM CoworkingSpaceFloorRoomBook csfp
    WHERE csfp.coworkingSpaceFloorRoom.id = :roomId AND (
        (:startTime <= csfp.startTime AND csfp.startTime <= :endTime AND :endTime <= csfp.endTime) OR
        (:startTime >= csfp.startTime AND :startTime <= csfp.endTime AND :endTime >= csfp.startTime AND :endTime <= csfp.endTime) OR
        (:startTime >= csfp.startTime AND :startTime <= csfp.endTime AND :endTime <= csfp.endTime) OR
        (:endTime >= csfp.endTime AND :startTime <= csfp.startTime)
    ) AND csfp.exitTime is null
    """)
    List<CoworkingAccountBookingResponse> findAccountsFloorRoomResponses(@Param("roomId") UUID roomId,
                                                                         @Param("startTime") LocalDateTime startTime,
                                                                         @Param("endTime") LocalDateTime endTime);

    @Query("""
    SELECT new ru.prod.feature.coworking.dto.CoworkingAccountBookingResponse(
        csfp.bookingAccount.login,
        csfp.startTime,
        csfp.endTime
    )
    FROM CoworkingSpaceFloorPlaceBook csfp
    WHERE csfp.coworkingSpaceFloorPlace.coworkingSpaceFloorRoom.id = :roomId AND (
        (:startTime <= csfp.startTime AND csfp.startTime <= :endTime AND :endTime <= csfp.endTime) OR
        (:startTime >= csfp.startTime AND :startTime <= csfp.endTime AND :endTime >= csfp.startTime AND :endTime <= csfp.endTime) OR
        (:startTime >= csfp.startTime AND :startTime <= csfp.endTime AND :endTime <= csfp.endTime) OR
        (:endTime >= csfp.endTime AND :startTime <= csfp.startTime)
    ) AND csfp.exitTime is null
    """)
    List<CoworkingAccountBookingResponse> findAccountsFloorPlaceResponsesByRoomId(@Param("roomId") UUID roomId,
                                                                                  @Param("startTime") LocalDateTime startTime,
                                                                                  @Param("endTime") LocalDateTime endTime);
}
