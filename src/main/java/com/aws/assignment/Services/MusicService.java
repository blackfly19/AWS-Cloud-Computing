package com.aws.assignment.Services;

import com.aws.assignment.Models.Music;
import com.aws.assignment.Repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MusicService {

    private final MusicRepository musicRepository;

    @Autowired
    public MusicService(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    public List<Music> getAllSongs() {
        // Code
    }
}
