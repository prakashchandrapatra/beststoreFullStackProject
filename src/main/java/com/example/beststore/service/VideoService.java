package com.example.beststore.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.beststore.models.Video;
import com.example.beststore.models.VideoMetadata;
import com.example.beststore.repository.VideoRepository;

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    // ==========================================
    // Constructor
    // ==========================================

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    // ==========================================
    // Normalize Category
    // ==========================================

    private String normalizeCategory(String category) {

        if (category == null || category.trim().isEmpty()) {
            return "other";
        }

        String key = category.trim().toLowerCase();

        switch (key) {

            // COMPUTERS / LAPTOPS
            case "computer":
            case "computers":
            case "laptop":
            case "laptops":
            case "notebook":
            case "notebooks":
            case "pc":
                return "computers";

            // PHONES
            case "phone":
            case "phones":
            case "smartphone":
            case "smartphones":
            case "mobile":
            case "mobiles":
                return "phone";

            // WATCHES
            case "watch":
            case "watches":
            case "smartwatch":
            case "smartwatches":
                return "watch";

            // OTHER
            case "other":
                return "other";

            default:
                return key;
        }
    }

    // ==========================================
    // Upload Video
    // Stores video directly in MySQL/Railway
    // ==========================================

    public Video uploadVideo(
            MultipartFile file,
            String category
    ) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IOException("Video file is empty");
        }

        Video video = new Video();

        // Original file name
        video.setName(file.getOriginalFilename());

        // Content type
        String contentType = file.getContentType();

        if (contentType == null || contentType.isBlank()) {
            contentType = "video/mp4";
        }

        video.setContentType(contentType);

        // Normalize category
        video.setCategory(normalizeCategory(category));

        // Store actual video inside MySQL/Railway
        video.setVideoData(file.getBytes());

        return videoRepository.save(video);
    }

    // ==========================================
    // Get Video By ID
    // ==========================================

    public Video getVideo(Long id) {

        return videoRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Video not found with id: " + id
                        )
                );
    }

    // ==========================================
    // Get Video Metadata By ID
    // ==========================================

    public VideoMetadata getVideoMeta(Long id) {

        Video video = getVideo(id);

        return new VideoMetadata(
                video.getId(),
                video.getName(),
                video.getContentType(),
                video.getCategory()
        );
    }

    // ==========================================
    // Get Video Metadata By Category
    // ==========================================

    public List<VideoMetadata> getVideosByCategory(String category) {

        String normalizedCategory =
                normalizeCategory(category);

        List<Object[]> rows =
                videoRepository.findVideoMetadataByCategory(
                        normalizedCategory
                );

        return rows.stream()
                .map(row -> new VideoMetadata(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        (String) row[3]
                ))
                .toList();
    }

    // ==========================================
    // Delete Video
    // Deletes video from MySQL/Railway
    // ==========================================

    public void deleteVideo(Long id) {

        Video video = getVideo(id);

        videoRepository.delete(video);
    }

    // ==========================================
    // Get All Videos
    // ==========================================

    public List<Video> getAllVideos() {

        List<Video> videos =
                videoRepository.findAll();

        // Do not return binary video data in JSON
        videos.forEach(
                video -> video.setVideoData(null)
        );

        return videos;
    }
}

