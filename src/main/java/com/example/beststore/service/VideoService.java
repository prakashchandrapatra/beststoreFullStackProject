


package com.example.beststore.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.beststore.models.Video;
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
    // ==========================================

    public Video uploadVideo(
            MultipartFile file,
            String category
    ) throws IOException {

        if (file == null || file.isEmpty()) {

            throw new IOException(
                    "Video file is empty"
            );
        }


        Video video = new Video();


        // Video filename

        video.setName(
                file.getOriginalFilename()
        );


        // Video content type

        String contentType =
                file.getContentType();

        if (contentType == null ||
            contentType.isBlank()) {

            contentType = "video/mp4";
        }

        video.setContentType(contentType);


        // Normalize category

        video.setCategory(
                normalizeCategory(category)
        );


        // Store actual video data in MySQL

        video.setVideoData(
                file.getBytes()
        );


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
    // Get ALL Videos By Category
    // ==========================================

    public List<Video> getVideosByCategory(
            String category
    ) {

        String normalizedCategory =
                normalizeCategory(category);


        List<Video> videos =
                videoRepository
                        .findByCategoryIgnoreCaseOrderByIdAsc(
                                normalizedCategory
                        );


        // Do NOT send video binary data to React

        videos.forEach(
                video -> video.setVideoData(null)
        );


        return videos;
    }


    // ==========================================
    // Delete Video
    // ==========================================

    public void deleteVideo(Long id) {

        if (!videoRepository.existsById(id)) {

            throw new RuntimeException(
                    "Video not found with id: " + id
            );
        }

        videoRepository.deleteById(id);
    }


    // ==========================================
    // Get All Videos
    // ==========================================

    public List<Video> getAllVideos() {

        List<Video> videos =
                videoRepository.findAll();


        // Don't send large video data

        videos.forEach(
                video -> video.setVideoData(null)
        );


        return videos;
    }
}