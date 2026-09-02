package com.example.beststore.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.beststore.models.Video;
import com.example.beststore.models.VideoMetadata;
import com.example.beststore.repository.VideoRepository;

@Service
public class VideoService {

private final VideoRepository videoRepository;

// Folder where video files are stored on disk.
// Falls back to ./data/videos if not set in application.properties.
@Value("${video.storage.dir:./data/videos}")
private String videoStorageDir;

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
// Upload Video (streams straight to disk, no full buffering)
// ==========================================

public Video uploadVideo(
        MultipartFile file,
        String category
) throws IOException {

    if (file == null || file.isEmpty()) {
        throw new IOException("Video file is empty");
    }

    // Ensure the storage directory exists
    Path storageDir = Paths.get(videoStorageDir);
    if (!Files.exists(storageDir)) {
        Files.createDirectories(storageDir);
    }

    // Build a unique on-disk filename to avoid collisions
    String storedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
    Path target = storageDir.resolve(storedName);

    // Streams the upload straight to disk, no full in-memory buffering
    file.transferTo(target);

    Video video = new Video();

    // File name
    video.setName(
            file.getOriginalFilename()
    );

    // Content type
    String contentType = file.getContentType();

    if (contentType == null || contentType.isBlank()) {
        contentType = "video/mp4";
    }

    video.setContentType(contentType);

    // Normalize category
    video.setCategory(
            normalizeCategory(category)
    );

    // Store only the on-disk filename, not the actual bytes
    video.setStoredFileName(storedName);

    return videoRepository.save(video);
}


// ==========================================
// Get Video By ID (entity, used internally)
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
// Get Video Metadata By ID (used by streaming controller)
// ==========================================

public VideoMetadata getVideoMeta(Long id) {

    Video video = getVideo(id);

    return new VideoMetadata(
            video.getId(),
            video.getName(),
            video.getContentType(),
            video.getCategory(),
            video.getStoredFileName()
    );
}


// ==========================================
// Get Video Metadata By Category
// ==========================================

public List<VideoMetadata> getVideosByCategory(String category) {

    String normalizedCategory = normalizeCategory(category);

    List<Object[]> rows =
            videoRepository.findVideoMetadataByCategory(
                    normalizedCategory
            );

    return rows.stream()
            .map(row -> new VideoMetadata(
                    ((Number) row[0]).longValue(),
                    (String) row[1],
                    (String) row[2],
                    (String) row[3],
                    (String) row[4]
            ))
            .toList();
}

// ==========================================
// Delete Video (removes DB row + file on disk)
// ==========================================

public void deleteVideo(Long id) throws IOException {

    Video video = getVideo(id);

    Path target = Paths.get(videoStorageDir, video.getStoredFileName());
    Files.deleteIfExists(target);

    videoRepository.deleteById(id);
}


// ==========================================
// Get All Videos
// ==========================================

//public List<Video> getAllVideos() {
//
//    List<Video> videos =
//            videoRepository.findAll();
//
//    // Do not return binary data
//    videos.forEach(
//            video -> video.setVideoData(null)
//    );
//
//    return videos;
//}


}

