package com.hospital.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.reserve.model.cmn.Dict;
import com.hospital.reserve.model.hosp.HospitalSet;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    List<Dict> findChildData(Long id);

    void exportDictData(HttpServletResponse response);

    void importDictData(MultipartFile file);
}
