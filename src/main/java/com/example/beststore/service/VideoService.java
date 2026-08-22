package com.example.beststore.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.beststore.models.Video;
import com.example.beststore.repository.VideoRepository;

@Service
public class VideoService {
	private final VideoRepository videoRepository;
	
	public VideoService(VideoRepository videoRepository) {
		this.videoRepository = videoRepository;
		
	}
	
	public Video uploadVideo(MultipartFile file, String category) throws IOException{
		Video video = new Video();
		video.setName(file.getOriginalFilename());
		video.setContentType(file.getContentType());
		video.setCategory(category);
		video.setVideoData(file.getBytes());
		return videoRepository.save(video);
	}

	public Video getVideo(Long id) {
		return videoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Video not found"));
	}
	
	public Optional<Video> getVideoByCategory(String  category){
		return videoRepository.findFirstByCategoryIgnoreCaseOrderByIdDesc(category);
	}
	
	public List<Video> getVideosByCategory(String category){
		List<Video>  videos = videoRepository.findByCategoryIgnoreCaseOrderByIdAsc(category);
		videos.forEach(v -> v.setVideoData(null));
		return videos;
		}
	public void deleteVideo(Long id) {
		videoRepository.deleteById(id);
	}


	public List<Video> getAllVideos() {
		// TODO Auto-generated method stub
		return null;
	}
}
