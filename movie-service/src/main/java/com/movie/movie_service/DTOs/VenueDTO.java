package com.movie.movie_service.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueDTO {
  private Long id;

  @NotBlank(message = "Venue name is required")
  private String name;

  @NotBlank(message = "Address is required")
  private String address;

  @NotBlank(message = "City is required")
  private String city;

  private String state;

  @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Invalid zip code format")
  private String zipCode;

  @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
  private String contactNumber;
}