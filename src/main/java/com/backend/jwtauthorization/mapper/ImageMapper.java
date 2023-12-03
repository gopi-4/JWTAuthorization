package com.backend.jwtauthorization.mapper;

import com.backend.jwtauthorization.dto.ImageDTO;
import com.backend.jwtauthorization.entity.Image;

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
