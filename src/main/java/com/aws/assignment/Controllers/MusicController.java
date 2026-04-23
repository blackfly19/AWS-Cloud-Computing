package com.aws.assignment.Controllers;

import com.aws.assignment.Services.MusicService;
import com.aws.assignment.Models.Music;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MusicController {

    private final MusicService musicService;

    @Autowired
    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping("/songs")
    public List<Music> getAllSongs() {
        return musicService.getAllSongs();
    }
}
