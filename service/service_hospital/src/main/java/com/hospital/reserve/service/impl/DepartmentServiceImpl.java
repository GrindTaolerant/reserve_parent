package com.hospital.reserve.service.impl;

import com.alibaba.fastjson.JSONObject;

import com.hospital.reserve.model.hosp.Department;
import com.hospital.reserve.repository.DepartmentRepository;
import com.hospital.reserve.service.DepartmentService;
import com.hospital.reserve.vo.hosp.DepartmentQueryVo;
import com.hospital.reserve.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        //paramMap to Department entity
        String paramMapString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(paramMapString, Department.class);

        Department departmentExist = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),
                department.getDepcode());

        if(departmentExist != null){
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        }else{
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        //create Pageable, to create current page and number per page
        //0 is the first page
        Pageable pageable = PageRequest.of(page-1, limit);


        //create Example
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Department> example = Example.of(department, matcher);


        Page<Department> all = departmentRepository.findAll(example, pageable);

        return all;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department != null){
            departmentRepository.deleteById(department.getId());
        }
    }

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {

        //create list
        List<DepartmentVo> result = new ArrayList<>();

        //check department info with hoscode
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);

        Example example = Example.of(departmentQuery);


        List<Department> departmentList = departmentRepository.findAll(example);

        //get child with bigcode
        Map<String, List<Department>> departmentMap =
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));

        //loop departmentMap
        for(Map.Entry<String, List<Department>> entry : departmentMap.entrySet()){
            //get bigcode
            String bigcode = entry.getKey();
            //get List with big code
            List<Department> departmentListBig = entry.getValue();

            //pack big dept
            DepartmentVo departmentVoB = new DepartmentVo();
            departmentVoB.setDepcode(bigcode);
            departmentVoB.setDepname(departmentListBig.get(0).getBigname());

            //pack child dept
            List<DepartmentVo> children = new ArrayList<>();
            for(Department department : departmentListBig){
                DepartmentVo departmentVoC = new DepartmentVo();
                departmentVoC.setDepcode(department.getDepcode());
                departmentVoC.setDepname(department.getDepname());
                children.add(departmentVoC);
            }
            //put children list under big
            departmentVoB.setChildren(children);

            result.add(departmentVoB);
        }

        return result;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department != null) {
            return department.getDepname();
        }
        return null;
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }
}
