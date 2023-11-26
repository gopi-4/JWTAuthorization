package com.backend.playground.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {

    private int Id;
    private String URL;
    private String Description;

    public ImageDTO(String URL, String description) {
        this.URL = URL;
        Description = description;
    }
}
