package ru.prod.feature.coworking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.prod.entity.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CoworkingRoomBookRepository extends JpaRepository<CoworkingSpaceFloorRoomBook, UUID> {

    @Query("""
    SELECT csfpb
    FROM CoworkingSpaceFloorPlaceBook csfpb
    WHERE csfpb.coworkingSpaceFloorPlace.coworkingSpaceFloorRoom.id = :roomId AND (
        (:startTime <= csfpb.startTime AND csfpb.startTime <= :endTime AND :endTime <= csfpb.endTime) OR
        (:startTime >= csfpb.startTime AND :startTime <= csfpb.endTime AND :endTime >= csfpb.startTime AND :endTime <= csfpb.endTime) OR
        (:startTime >= csfpb.startTime AND :startTime <= csfpb.endTime AND :endTime <= csfpb.endTime) OR
        (:endTime >= csfpb.endTime AND :startTime <= csfpb.startTime)
    ) AND csfpb.exitTime is null
    ORDER BY csfpb.endTime DESC
    """)
    List<CoworkingSpaceFloorPlaceBook> findBooksByCoworkingIdAndRoomId(@Param("roomId") UUID roomId,
                                                                       @Param("startTime") LocalDateTime startTime,
                                                                       @Param("endTime") LocalDateTime endTime);

    @Query("""
    SELECT csfpb
    FROM CoworkingSpaceFloorPlaceBook csfpb
    WHERE csfpb.id = :bookId AND (
        (:startTime <= csfpb.startTime AND csfpb.startTime <= :endTime AND :endTime <= csfpb.endTime) OR
        (:startTime >= csfpb.startTime AND :startTime <= csfpb.endTime AND :endTime >= csfpb.startTime AND :endTime <= csfpb.endTime) OR
        (:startTime >= csfpb.startTime AND :startTime <= csfpb.endTime AND :endTime <= csfpb.endTime) OR
        (:endTime >= csfpb.endTime AND :startTime <= csfpb.startTime)
    ) AND csfpb.exitTime is null
    ORDER BY csfpb.endTime DESC
    """)
    List<CoworkingSpaceFloorPlaceBook> findBooksByCoworkingIdAndBookId(@Param("bookId") UUID bookId,
                                                                       @Param("startTime") LocalDateTime startTime,
                                                                       @Param("endTime") LocalDateTime endTime);
    @Query("""
    SELECT csfrb
    FROM CoworkingSpaceFloorRoomBook csfrb
    WHERE csfrb.coworkingSpaceFloorRoom.id = :roomId AND csfrb.startTime <= :startTime AND csfrb.endTime >= :endTime AND csfrb.exitTime is null
    ORDER BY csfrb.endTime DESC
    """)
    List<CoworkingSpaceFloorRoomBook> findRoomBooksByCoworkingIdAndRoomId(@Param("roomId") UUID roomId,
                                                                          @Param("startTime") LocalDateTime startTime,
                                                                          @Param("endTime") LocalDateTime endTime);

    @Query("""
    SELECT csfrb
    FROM CoworkingSpaceFloorRoomBook csfrb
    WHERE
        csfrb.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.id = :coworkingId AND
        csfrb.coworkingSpaceFloorRoom.id = :roomId
    """)
    Optional<CoworkingSpaceFloorRoomBook> findBookByAccountIdAndCoworkingIdAndRoomId(@Param("accountId") UUID coworkingId,
                                                                                     @Param("roomId") UUID roomId);

    @Query("""
    SELECT csfrb
    FROM CoworkingSpaceFloorRoomBook csfrb
    WHERE csfrb.id = :bookId
    """)
    Optional<CoworkingSpaceFloorRoomBook> findBookByBookId(@Param("bookId") UUID bookId);
}
