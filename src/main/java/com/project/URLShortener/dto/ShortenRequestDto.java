package com.project.URLShortener.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class ShortenRequestDto {
    @NonNull
    private String longURL;
    private String alias;
}
