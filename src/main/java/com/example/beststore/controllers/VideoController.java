package com.example.beststore.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.beststore.models.Video;
import com.example.beststore.models.VideoMetadata;
import com.example.beststore.service.VideoService;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(
    origins = {
        "http://localhost:5173",
        "https://silly-alfajores-437c90.netlify.app"
    }
)
public class VideoController {

    private final VideoService videoService;

    // Falls back to ./data/videos if the property isn't set,
    // so a missing config value won't crash startup.
    @Value("${video.storage.dir:./data/videos}")
    private String videoStorageDir;

    // ==========================================
    // Constructor
    // ==========================================

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }


    // ==========================================
    // Upload Video
    // ==========================================

    @PostMapping("/upload")
    public ResponseEntity<Video> uploadVideo(
            @RequestParam("video") MultipartFile file,
            @RequestParam("category") String category
    ) throws Exception {

        Video video =
                videoService.uploadVideo(file, category);

        // No binary data on the entity anymore — nothing to null out.
        // (storedFileName is safe to return; it's just a filename, not the bytes.)

        return ResponseEntity.ok(video);
    }


    // ==========================================
    // Get Video Metadata By Category
    // ==========================================

    @GetMapping("/category/{category}/list")
    public ResponseEntity<List<VideoMetadata>> getVideosByCategory(
            @PathVariable String category
    ) {

        List<VideoMetadata> videos =
                videoService.getVideosByCategory(category);

        return ResponseEntity.ok(videos);
    }


    // ==========================================
    // Get / Play Actual Video By ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getVideo(@PathVariable Long id) {

        Video video = videoService.getVideo(id);

        if (video == null || video.getVideoData() == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource =
                new org.springframework.core.io.ByteArrayResource(video.getVideoData());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(video.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + video.getName() + "\""
                )
                .contentLength(video.getVideoData().length)
                .body(resource);
    }
   
    // ==========================================
    // Delete Video
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVideo(
            @PathVariable Long id
    ) throws IOException {

        videoService.deleteVideo(id);

        return ResponseEntity.ok(
                "Video deleted successfully"
        );
    }

}
