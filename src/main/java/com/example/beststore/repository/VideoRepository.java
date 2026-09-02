package com.example.beststore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beststore.models.Video;
import com.example.beststore.models.VideoMetadata;

public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByCategoryIgnoreCaseOrderByIdAsc(String category);
}
