package ru.prod.account;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.prod.common.exception.ImageStorageException;
import ru.prod.common.service.ImageStorageService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageStorageServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ImageStorageService imageStorageService;

    @Captor
    private ArgumentCaptor<PutObjectArgs> putObjectArgsCaptor;

    @BeforeEach
    void setUp() throws Exception {
        Field field = ImageStorageService.class.getDeclaredField("bucketName");
        field.setAccessible(true);
        field.set(imageStorageService, "test-bucket");
    }

    @Test
    void uploadFile_ShouldUploadAndReturnFileName() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String originalFilename = "test.png";
        byte[] fileBytes = "test content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(fileBytes);

        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(multipartFile.getContentType()).thenReturn("image/png");

        String uploadedFilename = imageStorageService.uploadFile(multipartFile);

        assertNotNull(uploadedFilename);
        assertTrue(uploadedFilename.endsWith(".png"));

        verify(minioClient).putObject(putObjectArgsCaptor.capture());

        PutObjectArgs capturedArgs = putObjectArgsCaptor.getValue();
        assertEquals("image/png", capturedArgs.contentType());
    }

    @Test
    void downloadFile_ShouldReturnInputStreamResource() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String fileName = "test.png";
        GetObjectResponse mockResponse = mock(GetObjectResponse.class);

        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockResponse);

        InputStreamResource resource = imageStorageService.downloadFile(fileName);

        assertNotNull(resource);
        verify(minioClient).getObject(any(GetObjectArgs.class));
    }

    @Test
    void uploadFile_ShouldThrowRuntimeException_WhenErrorOccurs() throws Exception {
        when(multipartFile.getInputStream()).thenThrow(new IOException());

        var exception = assertThrows(ImageStorageException.class, () ->
                imageStorageService.uploadFile(multipartFile)
        );

        assertTrue(exception.getMessage().contains("Error when uploading a file"));
        verify(minioClient, never()).putObject(any());
    }

    @Test
    void downloadFile_ShouldThrowRuntimeException_WhenMinioThrowsException() throws Exception {
        String fileName = "test.png";
        when(minioClient.getObject(any(GetObjectArgs.class))).thenThrow(new IOException());

        var exception = assertThrows(ImageStorageException.class, () ->
                imageStorageService.downloadFile(fileName)
        );

        assertTrue(exception.getMessage().contains("Error when downloading a file: test.png"));
    }

    @Test
    void uploadFile_InvalidExtension_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "invalid".getBytes());

        ImageStorageException exception = assertThrows(ImageStorageException.class, () -> imageStorageService.uploadFile(file));
        assertEquals("Invalid file format: txt", exception.getMessage());
    }

    @Test
    void uploadFile_NoExtension_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile("file", "invalidFile", "image/jpeg", "content".getBytes());

        ImageStorageException exception = assertThrows(ImageStorageException.class, () -> imageStorageService.uploadFile(file));
        assertEquals("File must have the extension jpg/png/jpeg", exception.getMessage());
    }

    @Test
    void uploadFile_MinIOError_ThrowsException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "content".getBytes());
        doThrow(new RuntimeException("MinIO failure"))
                .when(minioClient).putObject(any(PutObjectArgs.class));

        ImageStorageException exception = assertThrows(ImageStorageException.class, () -> imageStorageService.uploadFile(file));
        assertTrue(exception.getMessage().contains("Error when uploading a file"));
    }
}
