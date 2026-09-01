package com.example.beststore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.beststore.models.Video;
import com.example.beststore.models.VideoMetadata;

public interface VideoRepository extends JpaRepository<Video, Long> {

@Query("""
    SELECT new com.example.beststore.models.VideoMetadata(
        v.id,
        v.name,
        v.contentType,
        v.category
    )
    FROM Video v
    WHERE UPPER(v.category) = UPPER(:category)
    ORDER BY v.id ASC
""")
List<VideoMetadata> findVideoMetadataByCategory(
        @Param("category") String category
);


}
