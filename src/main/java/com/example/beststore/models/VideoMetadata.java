package com.example.beststore.models;

public class VideoMetadata {

private Long id;
private String name;
private String contentType;
private String category;

public VideoMetadata(
        Long id,
        String name,
        String contentType,
        String category
) {
    this.id = id;
    this.name = name;
    this.contentType = contentType;
    this.category = category;
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

}
