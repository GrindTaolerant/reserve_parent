package com.hospital.reserve.oss.controller;


import com.hospital.reserve.common.result.Result;
import com.hospital.reserve.oss.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/oss/file")
@RestController

public class FileApiController {

    @Autowired
    private FileService fileService;

    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file){
        String url = fileService.upload(file);
        return Result.ok(url);
    }
}
