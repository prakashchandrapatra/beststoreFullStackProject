package com.example.beststore.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "videos")
public class Video {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String contentType;
  private String category;//phone, Laptop, watch
  
  @Lob
  @Column(name = "video_data" ,columnDefinition = "LONGBLOB")
  private byte[] videoData;
  
  public Video() {}

  public Long getId() {
	return id;
  }

  public void setId(Long id) {
	this.id = id;
  }

  public String getName() {
	return name;
  }

  public void setName(String name) {
	this.name = name;
  }

  public String getContentType() {
	return contentType;
  }

  public void setContentType(String contentType) {
	this.contentType = contentType;
  }

  public String getCategory() {
	return category;
  }

  public void setCategory(String category) {
	this.category = category;
  }

  public byte[] getVideoData() {
	return videoData;
  }

  public void setVideoData(byte[] videoData) {
	this.videoData = videoData;
  }
  
  
}
