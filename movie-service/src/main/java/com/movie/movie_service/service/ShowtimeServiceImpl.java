package com.movie.movie_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.movie.movie_service.DTOs.ShowtimeDTO;
import com.movie.movie_service.exceptions.*;
import com.movie.movie_service.model.CinemaHall;
import com.movie.movie_service.model.Movie;
import com.movie.movie_service.model.Showtime;
import com.movie.movie_service.repository.CinemaHallRepository;
import com.movie.movie_service.repository.MovieRepository;
import com.movie.movie_service.repository.ShowtimeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowtimeServiceImpl {

  private final ShowtimeRepository showTimeRepository;
  private final MovieRepository movieRepository;
  private final CinemaHallRepository cinemaHallRepository;

  public ShowtimeServiceImpl(ShowtimeRepository showTimeRepository,
      MovieRepository movieRepository,
      CinemaHallRepository cinemaHallRepository) {
    this.showTimeRepository = showTimeRepository;
    this.movieRepository = movieRepository;
    this.cinemaHallRepository = cinemaHallRepository;
  }

  public List<ShowtimeDTO> getAllShowTimes() {
    return showTimeRepository.findAll().stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  public List<ShowtimeDTO> getShowTimesByMovie(Long movieId) {
    return showTimeRepository.findByMovieId(movieId).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  public List<ShowtimeDTO> getShowTimesByCinemaHall(Long cinemaHallId) {
    return showTimeRepository.findByCinemaHallId(cinemaHallId).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  public List<ShowtimeDTO> getUpcomingShowTimes() {
    return showTimeRepository.findByStartTimeAfter(LocalDateTime.now()).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  public ShowtimeDTO getShowTimeById(Long id) {
    Showtime showTime = showTimeRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Show time not found with id: " + id));
    return convertToDTO(showTime);
  }

  public ShowtimeDTO createShowTime(ShowtimeDTO showTimeDTO, String merchantUsername) {
    // Find movie and check ownership
    Movie movie = movieRepository.findById(showTimeDTO.getMovieId())
        .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + showTimeDTO.getMovieId()));

    if (!movie.getMerchantUsername().equals(merchantUsername)) {
      throw new UnauthorizedAccessException("You don't have permission to create showtimes for this movie");
    }

    // Find cinema hall and check ownership
    CinemaHall cinemaHall = cinemaHallRepository.findById(showTimeDTO.getCinemaHallId())
        .orElseThrow(
            () -> new ResourceNotFoundException("Cinema hall not found with id: " + showTimeDTO.getCinemaHallId()));

    if (!cinemaHall.getVenue().getMerchantUsername().equals(merchantUsername)) {
      throw new UnauthorizedAccessException("You don't have permission to create showtimes for this cinema hall");
    }

    // Calculate end time based on movie duration
    LocalDateTime endTime = showTimeDTO.getStartTime().plusMinutes(movie.getDurationMinutes());
    showTimeDTO.setEndTime(endTime);

    // If available seats not specified, use total seats from cinema hall
    if (showTimeDTO.getAvailableSeats() <= 0) {
      showTimeDTO.setAvailableSeats(cinemaHall.getTotalSeats());
    }

    // Validate no time conflicts with other shows in the same cinema hall
    validateNoTimeConflicts(showTimeDTO.getStartTime(), endTime, cinemaHall.getId(), null);

    Showtime showTime = convertToEntity(showTimeDTO);
    showTime.setMovie(movie);
    showTime.setCinemaHall(cinemaHall);

    Showtime savedShowTime = showTimeRepository.save(showTime);
    return convertToDTO(savedShowTime);
  }

  public ShowtimeDTO updateShowTime(Long id, ShowtimeDTO showTimeDTO, String merchantUsername) {
    Showtime existingShowTime = showTimeRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Show time not found with id: " + id));

    // Check if the merchant is the owner of the movie
    if (!existingShowTime.getMovie().getMerchantUsername().equals(merchantUsername)) {
      throw new UnauthorizedAccessException("You don't have permission to update this showtime");
    }

    // If movie is being changed, check ownership of new movie
    if (!showTimeDTO.getMovieId().equals(existingShowTime.getMovie().getId())) {
      Movie newMovie = movieRepository.findById(showTimeDTO.getMovieId())
          .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + showTimeDTO.getMovieId()));

      if (!newMovie.getMerchantUsername().equals(merchantUsername)) {
        throw new UnauthorizedAccessException("You don't have permission to assign this movie to the showtime");
      }

      existingShowTime.setMovie(newMovie);

      // Recalculate end time based on new movie's duration
      if (showTimeDTO.getStartTime() != null) {
        existingShowTime.setEndTime(showTimeDTO.getStartTime().plusMinutes(newMovie.getDurationMinutes()));
      } else {
        existingShowTime.setEndTime(existingShowTime.getStartTime().plusMinutes(newMovie.getDurationMinutes()));
      }
    }

    // If cinema hall is being changed, check ownership of new cinema hall
    if (!showTimeDTO.getCinemaHallId().equals(existingShowTime.getCinemaHall().getId())) {
      CinemaHall newCinemaHall = cinemaHallRepository.findById(showTimeDTO.getCinemaHallId())
          .orElseThrow(
              () -> new ResourceNotFoundException("Cinema hall not found with id: " + showTimeDTO.getCinemaHallId()));

      if (!newCinemaHall.getVenue().getMerchantUsername().equals(merchantUsername)) {
        throw new UnauthorizedAccessException("You don't have permission to assign this cinema hall to the showtime");
      }

      existingShowTime.setCinemaHall(newCinemaHall);
    }

    // If start time is changing, validate no conflicts
    if (showTimeDTO.getStartTime() != null && !showTimeDTO.getStartTime().equals(existingShowTime.getStartTime())) {
      LocalDateTime newEndTime;

      if (showTimeDTO.getMovieId().equals(existingShowTime.getMovie().getId())) {
        newEndTime = showTimeDTO.getStartTime().plusMinutes(existingShowTime.getMovie().getDurationMinutes());
      } else {
        Movie newMovie = movieRepository.findById(showTimeDTO.getMovieId()).get();
        newEndTime = showTimeDTO.getStartTime().plusMinutes(newMovie.getDurationMinutes());
      }

      validateNoTimeConflicts(showTimeDTO.getStartTime(), newEndTime,
          showTimeDTO.getCinemaHallId(), existingShowTime.getId());

      existingShowTime.setStartTime(showTimeDTO.getStartTime());
      existingShowTime.setEndTime(newEndTime);
    }

    // Update other fields
    if (showTimeDTO.getTicketPrice() >= 0) {
      existingShowTime.setPrice(showTimeDTO.getTicketPrice());
    }

    if (showTimeDTO.getAvailableSeats() >= 0) {
      existingShowTime.setAvailableSeats(showTimeDTO.getAvailableSeats());
    }

    existingShowTime.setActive(showTimeDTO.isActive());

    Showtime updatedShowTime = showTimeRepository.save(existingShowTime);
    return convertToDTO(updatedShowTime);
  }

  public void deleteShowTime(Long id, String merchantUsername) {
    Showtime showTime = showTimeRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Show time not found with id: " + id));

    // Check if the merchant is the owner of the movie or cinema hall
    if (!showTime.getMovie().getMerchantUsername().equals(merchantUsername) &&
        !showTime.getCinemaHall().getVenue().getMerchantUsername().equals(merchantUsername)) {
      throw new UnauthorizedAccessException("You don't have permission to delete this showtime");
    }

    showTimeRepository.delete(showTime);
  }

  private void validateNoTimeConflicts(LocalDateTime startTime, LocalDateTime endTime, Long cinemaHallId,
      Long excludeShowTimeId) {
    List<Showtime> existingShowTimes = showTimeRepository.findByCinemaHallId(cinemaHallId);

    for (Showtime existingShowTime : existingShowTimes) {
      // Skip the showtime being updated
      if (excludeShowTimeId != null && existingShowTime.getId().equals(excludeShowTimeId)) {
        continue;
      }

      // Check for overlapping time slots
      boolean hasConflict = (startTime.isBefore(existingShowTime.getEndTime()) &&
          endTime.isAfter(existingShowTime.getStartTime()));

      if (hasConflict) {
        throw new ValidationException("Time conflict with existing show: " +
            existingShowTime.getMovie().getTitle() + " at " +
            existingShowTime.getStartTime());
      }
    }
  }

  private ShowtimeDTO convertToDTO(Showtime showTime) {
    ShowtimeDTO showTimeDTO = new ShowtimeDTO();
    BeanUtils.copyProperties(showTime, showTimeDTO);
    showTimeDTO.setMovieId(showTime.getMovie().getId());
    showTimeDTO.setCinemaHallId(showTime.getCinemaHall().getId());
    return showTimeDTO;
  }

  public ShowtimeDTO setShowtimeStatus(Long id, boolean isActive, String merchantUsername) {
    Showtime showTime = showTimeRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Show time not found with id: " + id));

    // Check if the merchant is the owner of the movie or cinema hall
    if (!showTime.getMovie().getMerchantUsername().equals(merchantUsername) &&
        !showTime.getCinemaHall().getVenue().getMerchantUsername().equals(merchantUsername)) {
      throw new UnauthorizedAccessException("You don't have permission to update the status of this showtime");
    }

    showTime.setActive(isActive);
    showTimeRepository.save(showTime);
    return convertToDTO(showTime);
  }

  public List<ShowtimeDTO> getActiveShowtimes() {
    return showTimeRepository.findByIsActive(true).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  public List<ShowtimeDTO> getShowtimesByDate(LocalDateTime date) {
    return showTimeRepository.findByStartTimeBetween(
        date.toLocalDate().atStartOfDay(),
        date.toLocalDate().atTime(23, 59, 59)).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  private Showtime convertToEntity(ShowtimeDTO showTimeDTO) {
    Showtime showTime = new Showtime();
    BeanUtils.copyProperties(showTimeDTO, showTime, "movieId", "cinemaHallId");
    return showTime;
  }
}
