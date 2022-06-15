package com.capstone.booking.util;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FileUtil.class)
class FileUtilTest {

    @Test
    void upload_Success_Test() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.IMAGE_PNG_VALUE,
                "think of this as image".getBytes());

        String result = FileUtil.upload("no_path", file);
        boolean del = FileUtil.delete("no_path", result);

        assertNotNull(result);
        assertTrue(del);
    }

    @Test
    void upload_Error_Test() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.IMAGE_PNG_VALUE,
                "think of this as image".getBytes());

        assertThrows(Exception.class, () -> {
            String result = FileUtil.upload("Z:\\some_path_that\\didnt_exist\\", file);
        });
    }

    @Test
    void delete_Error_Test() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.IMAGE_PNG_VALUE,
                "think of this as image".getBytes());
           assertFalse(FileUtil.delete("some_path_that\\didnt_exist\\", "some_random_filename"));

    }

    @Test
    void download() {
    }

    @Test
    void getFileContent() {
    }
}