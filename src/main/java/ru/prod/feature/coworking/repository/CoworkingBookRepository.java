package ru.prod.feature.coworking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.prod.entity.*;
import ru.prod.feature.coworking.dto.CoworkingAccountBookResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CoworkingBookRepository extends JpaRepository<CoworkingSpaceFloorPlaceBook, UUID> {

    @Query("""
    SELECT csfp
    FROM CoworkingSpaceFloorPlace csfp
    WHERE csfp.id = :placeId AND csfp.coworkingSpaceFloor.coworkingSpace.id = :coworkingId
    """)
    Optional<CoworkingSpaceFloorPlace> findByCoworkingIdAndPlaceId(@Param("coworkingId") UUID coworkingId,
                                                                   @Param("placeId") UUID placeId);
    @Query("""
    SELECT csfpb
    FROM CoworkingSpaceFloorPlaceBook csfpb
    WHERE csfpb.coworkingSpaceFloorPlace.id = :placeId AND (
        (:startTime <= csfpb.startTime AND csfpb.startTime <= :endTime AND :endTime <= csfpb.endTime) OR
        (:startTime >= csfpb.startTime AND :startTime <= csfpb.endTime AND :endTime >= csfpb.startTime AND :endTime <= csfpb.endTime) OR
        (:startTime >= csfpb.startTime AND :startTime <= csfpb.endTime AND :endTime <= csfpb.endTime) OR
        (:endTime >= csfpb.endTime AND :startTime <= csfpb.startTime)
    ) AND csfpb.exitTime is null
    ORDER BY csfpb.endTime DESC
    """)
    List<CoworkingSpaceFloorPlaceBook> findBookByCoworkingIdAndPlaceId(@Param("placeId") UUID placeId,
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
    List<CoworkingSpaceFloorPlaceBook> findBookByBookId(@Param("bookId") UUID bookId,
                                                        @Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);

    @Query("""
    SELECT csfp
    FROM CoworkingSpaceFloorRoom csfp
    WHERE csfp.id = :roomId AND csfp.coworkingSpaceFloor.coworkingSpace.id = :coworkingId
    """)
    Optional<CoworkingSpaceFloorRoom> findByCoworkingIdAndRoomId(@Param("coworkingId") UUID coworkingId,
                                                                 @Param("roomId") UUID roomId);

    @Query("""
    SELECT csfpb
    FROM CoworkingSpaceFloorPlaceBook csfpb
    WHERE
        csfpb.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.id = :coworkingId AND
        csfpb.coworkingSpaceFloorPlace.id = :placeId
    """)
    Optional<CoworkingSpaceFloorPlaceBook> findBookByCoworkingIdAndRoomId(@Param("coworkingId") UUID coworkingId,
                                                                          @Param("placeId") UUID placeId);

    @Query("""
    SELECT csfpb
    FROM CoworkingSpaceFloorPlaceBook csfpb
    WHERE csfpb.id = :bookId
    """)
    Optional<CoworkingSpaceFloorPlaceBook> findBookByBookId(@Param("bookId") UUID bookId);

    @Query("""
    SELECT new ru.prod.feature.coworking.dto.CoworkingAccountBookResponse(
        b.id,
        new ru.prod.feature.coworking.dto.CoworkingSpaceBookResponse(
          b.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.id,
          b.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.location,
          b.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.title,
          b.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.time,
          b.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.description) AS coworking,
        new ru.prod.feature.account.dto.AccountProfileResponse(
            b.bookingAccount.login,
            b.bookingAccount.firstName,
            b.bookingAccount.lastName,
            b.bookingAccount.birthDay,
            b.bookingAccount.avatarFileName,
            b.bookingAccount.isPrivate,
            b.bookingAccount.isAdmin
        ),
        b.startTime,
        b.endTime,
        b.createdAt,
        b.coworkingSpaceFloorPlace.price,
        b.coworkingSpaceFloorPlace.latitude,
        b.coworkingSpaceFloorPlace.longitude,
        false
    )
    FROM CoworkingSpaceFloorPlaceBook b
    WHERE b.bookingAccount.id = :accountId
    AND CURRENT_TIMESTAMP < b.endTime
    """)
    Page<CoworkingAccountBookResponse> findActivePlaceBookingsByAccount(@Param("accountId") UUID accountId, Pageable pageable);

    @Query("""
    SELECT new ru.prod.feature.coworking.dto.CoworkingAccountBookResponse(
        b.id,
        new ru.prod.feature.coworking.dto.CoworkingSpaceBookResponse(
          b.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.id,
          b.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.location,
          b.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.title,
          b.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.time,
          b.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.description) AS coworking,
        new ru.prod.feature.account.dto.AccountProfileResponse(
            b.bookingAccount.login,
            b.bookingAccount.firstName,
            b.bookingAccount.lastName,
            b.bookingAccount.birthDay,
            b.bookingAccount.avatarFileName,
            b.bookingAccount.isPrivate,
            b.bookingAccount.isAdmin
        ),
        b.startTime,
        b.endTime,
        b.createdAt,
        b.coworkingSpaceFloorPlace.price,
        b.coworkingSpaceFloorPlace.latitude,
        b.coworkingSpaceFloorPlace.longitude,
        false
    )
    FROM CoworkingSpaceFloorPlaceBook b
    """)
    Page<CoworkingAccountBookResponse> findAllPlaceBookings(Pageable pageable);

    @Query("""
    SELECT new ru.prod.feature.coworking.dto.CoworkingAccountBookResponse(
        b.id,
        new ru.prod.feature.coworking.dto.CoworkingSpaceBookResponse(
          b.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.id,
          b.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.location,
          b.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.title,
          b.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.time,
          b.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.description) AS coworking,
        new ru.prod.feature.account.dto.AccountProfileResponse(
          b.bookingAccount.login,
          b.bookingAccount.firstName,
          b.bookingAccount.lastName,
          b.bookingAccount.birthDay,
          b.bookingAccount.avatarFileName,
          b.bookingAccount.isPrivate,
          b.bookingAccount.isAdmin
        ),
        b.startTime,
        b.endTime,
        b.createdAt,
        b.coworkingSpaceFloorRoom.price,
        b.coworkingSpaceFloorRoom.latitude,
        b.coworkingSpaceFloorRoom.longitude,
        true
    )
    FROM CoworkingSpaceFloorRoomBook b
    WHERE b.bookingAccount.id = :accountId
    AND CURRENT_TIMESTAMP < b.endTime
    """)
    Page<CoworkingAccountBookResponse> findActiveRoomBookingsByAccount(@Param("accountId") UUID accountId, Pageable pageable);


    @Query("""
    SELECT new ru.prod.feature.coworking.dto.CoworkingAccountBookResponse(
        b.id,
        new ru.prod.feature.coworking.dto.CoworkingSpaceBookResponse(
          b.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.id,
          b.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.location,
          b.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.title,
          b.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.time,
          b.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.description) AS coworking,
        new ru.prod.feature.account.dto.AccountProfileResponse(
            b.bookingAccount.login,
            b.bookingAccount.firstName,
            b.bookingAccount.lastName,
            b.bookingAccount.birthDay,
            b.bookingAccount.avatarFileName,
            b.bookingAccount.isPrivate,
            b.bookingAccount.isAdmin
        ),
        b.startTime,
        b.endTime,
        b.createdAt,
        b.coworkingSpaceFloorRoom.price,
        b.coworkingSpaceFloorRoom.latitude,
        b.coworkingSpaceFloorRoom.longitude,
        true
    )
    FROM CoworkingSpaceFloorRoomBook b
    """)
    Page<CoworkingAccountBookResponse> findAllRoomBookings(Pageable pageable);

    @Query("""
    SELECT new ru.prod.feature.coworking.dto.CoworkingAccountBookResponse(
        b.id,
        new ru.prod.feature.coworking.dto.CoworkingSpaceBookResponse(
          b.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.id,
          b.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.location,
          b.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.title,
          b.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.time,
          b.coworkingSpaceFloorRoom.coworkingSpaceFloor.coworkingSpace.description) AS coworking,
        new ru.prod.feature.account.dto.AccountProfileResponse(
          b.bookingAccount.login,
          b.bookingAccount.firstName,
          b.bookingAccount.lastName,
          b.bookingAccount.birthDay,
          b.bookingAccount.avatarFileName,
          b.bookingAccount.isPrivate,
          b.bookingAccount.isAdmin
        ),
        b.startTime,
        b.endTime,
        b.createdAt,
        b.coworkingSpaceFloorRoom.price,
        b.coworkingSpaceFloorRoom.latitude,
        b.coworkingSpaceFloorRoom.longitude,
        true
    )
    FROM CoworkingSpaceFloorRoomBook b
    WHERE b.bookingAccount.id = :accountId
    """)
    Page<CoworkingAccountBookResponse> findAccountRoomBookings(UUID accountId, Pageable pageable);

    @Query("""
    SELECT new ru.prod.feature.coworking.dto.CoworkingAccountBookResponse(
        b.id,
        new ru.prod.feature.coworking.dto.CoworkingSpaceBookResponse(
          b.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.id,
          b.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.location,
          b.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.title,
          b.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.time,
          b.coworkingSpaceFloorPlace.coworkingSpaceFloor.coworkingSpace.description) AS coworking,
        new ru.prod.feature.account.dto.AccountProfileResponse(
            b.bookingAccount.login,
            b.bookingAccount.firstName,
            b.bookingAccount.lastName,
            b.bookingAccount.birthDay,
            b.bookingAccount.avatarFileName,
            b.bookingAccount.isPrivate,
            b.bookingAccount.isAdmin
        ),
        b.startTime,
        b.endTime,
        b.createdAt,
        b.coworkingSpaceFloorPlace.price,
        b.coworkingSpaceFloorPlace.latitude,
        b.coworkingSpaceFloorPlace.longitude,
        false
    )
    FROM CoworkingSpaceFloorPlaceBook b
    WHERE b.bookingAccount.id = :accountId
    """)
    Page<CoworkingAccountBookResponse> findAccountPlaceBookings(UUID accountId, Pageable pageable);
}
