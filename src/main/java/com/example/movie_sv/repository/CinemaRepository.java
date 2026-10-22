package com.example.movie_sv.repository;
import com.example.movie_sv.model.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CinemaRepository extends JpaRepository<Cinema, Long> {}
