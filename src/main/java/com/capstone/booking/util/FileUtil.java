package com.capstone.booking.util;

import com.capstone.booking.constant.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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

    public static ResponseEntity<Object> getFileContent(String path, String filename) {
        try{
            File file = new File(path + filename);
            log.info("Getting file data with file name \"{}\" ", file.getAbsolutePath());
            byte[] imageByte = FileUtils.readFileToByteArray(file);
            return ResponseEntity.ok().body(imageByte);
        } catch (FileNotFoundException e) {
            log.error("File with name \"{}\" not found", filename);
            return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND, null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.info("Error occurred while trying to get data with filename {}. Error : {}", filename, e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
