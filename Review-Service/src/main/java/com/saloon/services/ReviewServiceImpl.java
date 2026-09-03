package com.saloon.services;

import com.saloon.dtos.ReviewRequest;
import com.saloon.dtos.SaloonDto;
import com.saloon.dtos.UserDto;
import com.saloon.models.Review;
import com.saloon.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{

    private final ReviewRepository reviewRepository;


    @Override
    public Review createReview(ReviewRequest reviewRequest, UserDto userDto, SaloonDto saloonDto) {
        Review review = new Review();
        review.setReviewText(reviewRequest.getReviewText());
        review.setRating(reviewRequest.getRating());
        review.setUserId(userDto.getId());
        review.setSaloonId(saloonDto.getId());
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> findAllBySaloonId(Long saloonId) {
        return reviewRepository.findBySaloonId(saloonId);
    }

    private Review getReviewById(Long id){
        return reviewRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Review not exist."));
    }
    @Override
    public Review updateReview(ReviewRequest reviewRequest, Long reviewId, Long userId) {
        Review review = getReviewById(reviewId);
        if(!review.getUserId().equals(userId)){
            throw new RuntimeException("You don't have permission to update this review.");
        }
        review.setReviewText(reviewRequest.getReviewText());
        review.setRating(reviewRequest.getRating());
        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) {
        Review review = getReviewById(reviewId);
        if(!review.getUserId().equals(userId)){
            throw new RuntimeException("You don't have permission to delete this review.");
        }
        reviewRepository.delete(review);
    }
}
