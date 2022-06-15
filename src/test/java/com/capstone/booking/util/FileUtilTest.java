package com.capstone.booking.util;

import com.capstone.booking.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FileUtil.class)
class FileUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void upload_Success_Test() {

    }

    @Test
    void download() {
    }

    @Test
    void getFileContent() {
    }
}