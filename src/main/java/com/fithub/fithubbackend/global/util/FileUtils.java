package com.fithub.fithubbackend.global.util;

import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

    private static final Tika tika = new Tika();

    public static void isValidDocument(List<MultipartFile> images) {
        for (MultipartFile image : images) {
            try (InputStream inputStream = image.getInputStream()) {
                boolean isValid = validImageFile(inputStream);
                if (!isValid) {
                    throw new CustomException(ErrorCode.INVALID_IMAGE, "이미지 파일이 아닌 파일");
                }
            } catch (IOException e) {
                throw new CustomException(ErrorCode.INVALID_IMAGE, "이미지 확장자 검사 실패");
            }
        }
    }

    public static boolean validImageFile(InputStream inputStream) {
        try (InputStream clonedStream = new ByteArrayInputStream(IOUtils.toByteArray(inputStream))) {
            List<String> whiteList = Arrays.asList("image/jpeg", "image/pjpeg", "image/png", "image/gif", "image/bmp", "image/x-windows-bmp");
            String mimeType = tika.detect(clonedStream);

            return whiteList.stream().anyMatch(notValidType -> notValidType.equalsIgnoreCase(mimeType));
        } catch (IOException e) {
            return false;
        }
    }
}