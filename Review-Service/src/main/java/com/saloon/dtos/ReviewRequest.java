package com.saloon.dtos;

import lombok.Data;

@Data
public class ReviewRequest {


    private String reviewText;
    private double rating;

}
