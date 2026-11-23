package com.agrireach.storage;

public class StorageObjectMetadata {

    private final long contentLength;
    private final String contentType;

    public StorageObjectMetadata(
        long contentLength,
        String contentType
    ) {
        this.contentLength = contentLength;
        this.contentType = contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }
}