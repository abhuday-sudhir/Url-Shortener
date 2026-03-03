package com.project.URLShortener.controller;

import com.project.URLShortener.dto.ShortenRequestDto;
import com.project.URLShortener.service.ShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class ShortenerController {

    @Autowired
    private ShortenerService shortenerService;

    @PostMapping("/shorten")
    public ResponseEntity<String> shorten(@RequestBody ShortenRequestDto request)
    {
        String shortCode=shortenerService.shorten(request.getLongURL(),request.getAlias());
        return new ResponseEntity<>(shortCode,HttpStatus.OK);
    }

    @GetMapping("/get/{shortUrl}")
    public ResponseEntity<?> redirect(@PathVariable String shortUrl)
    {
        String longUrl=shortenerService.redirect(shortUrl);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(longUrl)).build();
    }

}