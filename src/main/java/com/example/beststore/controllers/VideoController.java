package com.example.beststore.controllers;

import java.util.List;

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
import com.example.beststore.service.VideoService;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "http://localhost:5173")
public class VideoController {
   
	private final VideoService videoService;
	//constructor injection
	public VideoController(VideoService videoService) {
		this.videoService = videoService;
	}
	//1.UploadVideo
	@PostMapping("/upload")
	public ResponseEntity<Video> uploadVideo(
			@RequestParam("video") MultipartFile file,
			@RequestParam("category") String category) throws Exception{
		Video video = videoService.uploadVideo(file, category);
		//Don't send large binary data back in JSON response
		video.setVideoData(null);
		return ResponseEntity.ok(video);
	}

	  // =========================================================
    // 3. Get All Videos - Metadata Only
    // =========================================================
    @GetMapping("/category/{category}/list")
    public ResponseEntity<List<Video>> getVideosByCategory(@PathVariable String category) {

        List<Video> videos = videoService.getVideosByCategory(category);

        // Remove binary data before sending JSON

        return ResponseEntity.ok(videos);
    }


    // =========================================================
    // 4. Get/Play Actual Video By ID
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getVideo(
            @PathVariable Long id) {

        Video video = videoService.getVideo(id);

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                video.getContentType()
                        )
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                video.getName() +
                                "\""
                )
                .body(video.getVideoData());
    }


    // =========================================================
    // 5. Delete Video
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVideo(
            @PathVariable Long id) {

        videoService.deleteVideo(id);

        return ResponseEntity.ok(
                "Video deleted successfully"
        );
    }
}
