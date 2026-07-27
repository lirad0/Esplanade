package com.KartaGalaxy.backend.services;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    public String getFileType(InputStream stream) throws IOException {
        return getFileType(stream.readAllBytes());
    }

    public String getFileType(byte[] bytes) throws IOException {
        Tika tika = new Tika();
        return tika.detect(bytes); // e.g. "image/jpeg"
    }

}
