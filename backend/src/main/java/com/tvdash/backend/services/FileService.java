package com.tvdash.backend.services;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    public String getFileType(InputStream stream) throws IOException {
        Tika tika = new Tika();
        // Tika needs a stream that supports mark/reset, or pass bytes directly
        byte[] bytes = stream.readAllBytes();

        return tika.detect(bytes); // e.g. "image/jpeg"
    }

}
