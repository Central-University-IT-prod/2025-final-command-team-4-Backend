package ru.prod.common.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.prod.common.exception.ImageStorageException;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "png", "jpeg");

    public String uploadFile(MultipartFile file) {
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
            String filename = UUID.randomUUID() + "." + getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return filename;
        } catch (ImageStorageException e) {
            throw e;
        } catch (Exception e) {
            throw new ImageStorageException("Error when uploading a file: " + file.getOriginalFilename(), e);
        }
    }

    private String getFileExtension(String filename) {
        if (!filename.contains(".")) {
            throw new ImageStorageException("File must have the extension jpg/png/jpeg");
        }

        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ImageStorageException("Invalid file format: " + extension);
        }

        return extension;
    }

    public InputStreamResource downloadFile(String fileName) {
        InputStream stream;
        try {
            stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new ImageStorageException("Error when downloading a file: " + fileName, e);
        }

        return new InputStreamResource(stream);
    }
}
