package com.aws.assignment.Services;

import com.aws.assignment.Models.Music;
import com.aws.assignment.Repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Collections;

@Service
public class MusicService {

    private final MusicRepository musicRepository;

    public MusicService(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    public List<Music> getAllSongs() {
        return Collections.emptyList();
    }
}
