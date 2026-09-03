package com.saloon.services;

import com.saloon.dtos.ReviewRequest;
import com.saloon.dtos.SaloonDto;
import com.saloon.dtos.UserDto;
import com.saloon.models.Review;

import java.util.List;

public interface ReviewService {

    Review createReview(ReviewRequest reviewRequest, UserDto userDto, SaloonDto saloonDto);
    List<Review> findAllBySaloonId(Long saloonId);
    Review updateReview(ReviewRequest reviewRequest,Long reviewId, Long userId);
    void deleteReview(Long reviewId, Long userId);

}
