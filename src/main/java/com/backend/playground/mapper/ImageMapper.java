package com.backend.playground.mapper;

import com.backend.playground.dto.ImageDTO;
import com.backend.playground.entity.Image;

public class ImageMapper {

    public static Image mapDtoToEntity(ImageDTO imageDTO) {
        return Image.builder()
                .URL(imageDTO.getURL())
                .Description(imageDTO.getDescription())
                .build();
    }

    public static ImageDTO mapEntityToDto(Image image) {
        return ImageDTO.builder()
                .URL(image.getURL())
                .Description(image.getDescription())
                .build();
    }
}
