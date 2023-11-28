package com.backend.playground.controller;

import com.backend.playground.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/user/image")
public class ImageController {
    @Autowired
    private ImageService imageService;
}
