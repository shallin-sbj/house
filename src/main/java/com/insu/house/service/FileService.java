package com.insu.house.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件服务
 */
public interface FileService {

    List<String> getImgPaths(List<MultipartFile> files);

    String getResourcePath();

}
