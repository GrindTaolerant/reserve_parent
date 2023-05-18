package com.hospital.reserve.oss.service;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface FileService {


    String upload(MultipartFile file);
}
