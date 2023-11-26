package com.backend.playground.mapper;

import com.backend.playground.dto.ImageDTO;
import com.backend.playground.entity.Image;

public class ImageMapper {

    public static Image mapDtoToEntity(ImageDTO imageDTO) {
        Image image = new Image();
        image.setId(imageDTO.getId());
        image.setURL(imageDTO.getURL());
        image.setDescription(imageDTO.getDescription());
        return image;
    }

    public static ImageDTO mapEntityToDto(Image image) {
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setId(image.getId());
        imageDTO.setURL(image.getURL());
        imageDTO.setDescription(image.getDescription());
        return imageDTO;
    }
}
