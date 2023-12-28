package com.fithub.fithubbackend.global.util;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FileUtils {

    private static final Tika tika = new Tika();

    public static boolean validImageFile(InputStream inputStream) {
        try (InputStream clonedStream = new ByteArrayInputStream(IOUtils.toByteArray(inputStream))) {
            List<String> whiteList = Arrays.asList("image/jpeg", "image/pjpeg", "image/png", "image/gif", "image/bmp", "image/x-windows-bmp");
            String mimeType = tika.detect(clonedStream);

            boolean isValid = whiteList.stream().anyMatch(notValidType -> notValidType.equalsIgnoreCase(mimeType));
            return isValid;
        } catch (IOException e) {
            return false;
        }

    }
}