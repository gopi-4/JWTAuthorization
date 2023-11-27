package com.backend.playground.controller;

import com.backend.playground.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/user/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
}
