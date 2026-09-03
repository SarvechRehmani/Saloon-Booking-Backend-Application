package com.saloon.controllers;

import com.saloon.dtos.ApiResponse;
import com.saloon.dtos.ReviewRequest;
import com.saloon.dtos.SaloonDto;
import com.saloon.dtos.UserDto;
import com.saloon.models.Review;
import com.saloon.services.ReviewService;
import com.saloon.services.clients.SaloonFeignClient;
import com.saloon.services.clients.UserFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserFeignClient userFeignClient;
    private final SaloonFeignClient saloonFeignClient;

    @PostMapping("/saloon/{saloonId}")
    public ResponseEntity<Review> createReview(@PathVariable Long saloonId, @RequestBody ReviewRequest reviewRequest, @RequestHeader("Authorization") String jwt){

        UserDto userDto = userFeignClient.getUserProfile(jwt).getBody();
        SaloonDto saloonDto = saloonFeignClient.getSaloonById(saloonId).getBody();
        Review review = reviewService.createReview(reviewRequest,userDto,saloonDto);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/saloon/{saloonId}")
    public ResponseEntity<List<Review>> getReviewsBySaloonId(@PathVariable Long saloonId, @RequestHeader("Authorization") String jwt){

        SaloonDto saloonDto = saloonFeignClient.getSaloonById(saloonId).getBody();
        List<Review> reviews = reviewService.findAllBySaloonId(saloonDto.getId());
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable Long reviewId, @RequestBody ReviewRequest reviewRequest, @RequestHeader("Authorization") String jwt){
        UserDto userDto = userFeignClient.getUserProfile(jwt).getBody();
        Review review = reviewService.updateReview(reviewRequest,reviewId,userDto.getId());
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Long reviewId, @RequestBody ReviewRequest reviewRequest, @RequestHeader("Authorization") String jwt){
        UserDto userDto = userFeignClient.getUserProfile(jwt).getBody();
        reviewService.deleteReview(reviewId,userDto.getId());
        ApiResponse response = new ApiResponse();
        response.setMessage("Review deleted.");
        return ResponseEntity.ok(response);
    }

}
