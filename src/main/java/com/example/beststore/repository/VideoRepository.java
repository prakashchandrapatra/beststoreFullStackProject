package com.example.beststore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.beststore.models.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query(value = """
        SELECT
            v.id AS id,
            v.name AS name,
            v.content_type AS contentType,
            v.category AS category
        FROM videos v
        WHERE LOWER(v.category) = LOWER(:category)
        ORDER BY v.id ASC
        """, nativeQuery = true)
    List<VideoMetadataProjection> findVideoMetadataByCategory(
            @Param("category") String category
    );
}