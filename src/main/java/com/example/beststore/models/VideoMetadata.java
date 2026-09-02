package com.example.beststore.models;

public class VideoMetadata {

private Long id;
private String name;
private String contentType;
private String category;
private String storedFileName;

public VideoMetadata(
        Long id,
        String name,
        String contentType,
        String category,
        String storedFileName
) {
    this.id = id;
    this.name = name;
    this.contentType = contentType;
    this.category = category;
    this.storedFileName = storedFileName;
}

public Long getId() {
    return id;
}

public String getName() {
    return name;
}

public String getContentType() {
    return contentType;
}

public String getCategory() {
    return category;
}

public String getStoredFileName() {
	// TODO Auto-generated method stub
	return storedFileName;
}

}
