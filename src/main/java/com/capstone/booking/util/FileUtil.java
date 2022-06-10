package com.capstone.booking.util;

import com.capstone.booking.constant.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.coyote.Response;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
public class FileUtil {
    private FileUtil() {}

    public static String upload(String path, MultipartFile file) throws IOException {
        UUID filename = UUID.randomUUID();
        String newFileName = filename + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        File filepath = new File(path + newFileName);
        if (filepath.createNewFile()) {
            log.info("File successfully created in : {}", filepath);
            try (FileOutputStream outputStream = new FileOutputStream(filepath)){
                outputStream.write(file.getBytes());
                return newFileName;
            } catch (Exception e) {
                log.error("Error when trying to write data to the file. Error : {}", e.getMessage());
                return null;
            }
        } else {
            log.info("File with name {} already exist", filename);
            return null;
        }
    }

    public static ResponseEntity<Object> download(String path, String filename) {
        log.info("Sending File with name : {}", filename);
        Path filePath = Paths.get(path + filename);
        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (Exception e) {
            log.error("Error occurred while trying to create resource URL. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND, null, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
