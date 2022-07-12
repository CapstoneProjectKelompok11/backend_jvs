package com.capstone.booking.service;

import com.capstone.booking.domain.common.ApiResponse;
import com.capstone.booking.domain.dao.Building;
import com.capstone.booking.domain.dao.Favorite;
import com.capstone.booking.domain.dao.User;
import com.capstone.booking.domain.dto.BuildingRequest;
import com.capstone.booking.domain.dto.FavoriteResponse;
import com.capstone.booking.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FavoriteService.class)
class FavoriteServiceTest {

    @MockBean
    private BuildingRepository buildingRepository;

    @MockBean
    private FavoriteRepository favoriteRepository;

    @MockBean
    private FloorRepository floorRepository;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private FavoriteService favoriteService;

    @Test
    void getFavorite_Success_Test() {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();
        BuildingRequest request = BuildingRequest.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();
        User user = User.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        Favorite favorite = Favorite.builder()
                .building(building)
                .user(user)
                .build();
        List<Favorite> favorites = new ArrayList<>();
        favorites.add(favorite);
        Set<String> type = Set.of("Tipe 1", "Tipe 2");

        Double rating = 4.8;

        int floorCount = 3;

        when(favoriteRepository.findFavoriteByUserEmailAndBuildingIsDeletedFalse(anyString())).thenReturn(favorites);
        when(floorRepository.findDistinctTypeByBuildingId(anyLong())).thenReturn(type);
        when(reviewRepository.averageOfBuildingReviewRating(anyLong())).thenReturn(rating);
        when(floorRepository.countByBuilding_Id(anyLong())).thenReturn(floorCount);
        when(modelMapper.map(any(), eq(BuildingRequest.class))).thenReturn(request);

        ResponseEntity<Object> responseEntity = favoriteService.getFavorite("user-email@gmail.com");
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        List<FavoriteResponse> result = ((List<FavoriteResponse>) apiResponse.getData());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Gedung A", result.get(0).getBuilding().getName());
        assertEquals("Jalan A", result.get(0).getBuilding().getAddress());
        assertEquals(100, result.get(0).getBuilding().getCapacity());
        assertEquals(rating, result.get(0).getBuilding().getRating());
        assertEquals(type, result.get(0).getBuilding().getOfficeType());
        assertEquals(floorCount, result.get(0).getBuilding().getFloorCount());
    }

    @Test
    void getFavorite_Error_Test() {

        when(favoriteRepository.findFavoriteByUserEmailAndBuildingIsDeletedFalse(anyString())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = favoriteService.getFavorite("user-email@gmail.com");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void unFavorite_Success_Test() {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();
        User user = User.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        Favorite favorite = Favorite.builder()
                .building(building)
                .user(user)
                .build();

        when(favoriteRepository.findFavorite(anyString(), anyLong())).thenReturn(Optional.of(favorite));
        doNothing().when(favoriteRepository).delete(any());

        ResponseEntity<Object> responseEntity = favoriteService.unFavorite("user-email@gmail.com", 1L);
        verify(favoriteRepository, times(1)).delete(any());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void unFavorite_FavoriteEmpty_Test() {

        when(favoriteRepository.findFavorite(anyString(), anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = favoriteService.unFavorite("user-email@gmail.com", 1L);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void unFavorite_Error_Test() {

        when(favoriteRepository.findFavorite(anyString(), anyLong())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = favoriteService.unFavorite("user-email@gmail.com", 1L);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void addToFavorites_Success_Test() {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();
        User user = User.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        Favorite favorite = Favorite.builder()
                .building(building)
                .user(user)
                .build();

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(building));
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(favoriteRepository.save(any())).thenReturn(favorite);

        ResponseEntity<Object> responseEntity = favoriteService.addToFavorites("user-email@gmail.com", 1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    }

    @Test
    void addToFavorites_BuildingEmpty_Test() {


        when(buildingRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = favoriteService.addToFavorites("user-email@gmail.com", 1L);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    }

    @Test
    void addToFavorites_UserEmpty_Test() {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(building));
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = favoriteService.addToFavorites("user-email@gmail.com", 1L);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    }

    @Test
    void addToFavorites_Error_Test() {

        when(buildingRepository.findById(anyLong())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = favoriteService.addToFavorites("user-email@gmail.com", 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

    }
}