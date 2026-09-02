package com.example.beststore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.beststore.models.Video;
import com.example.beststore.models.VideoMetadata;

public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query(value = """
        SELECT
            v.id,
            v.name,
            v.content_type,
            v.category
            v.storedFileName
        FROM videos
        WHERE LOWER(v.category) = LOWER(:category)
        ORDER BY id ASC
        """, nativeQuery = true)
    List<Object[]> findVideoMetadataByCategory(
            @Param("category") String category
    );
}
