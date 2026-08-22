package com.example.beststore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beststore.models.Video;

public interface VideoRepository extends JpaRepository<Video, Long>{
 
 List<Video> findByCategoryIgnoreCaseOrderByIdAsc(String category);

 Optional<Video> findFirstByCategoryIgnoreCaseOrderByIdDesc(String category);

 Optional<Video> findById(Long id);

 void deleteById(Long id);
}
