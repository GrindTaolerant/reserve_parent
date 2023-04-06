package com.hospital.reserve.common.exception;

import com.hospital.reserve.common.result.Result;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice

public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(HospitalException.class)
    @ResponseBody
    public Result error(HospitalException e){
        return Result.build(e.getCode(), e.getMessage());
    }
}



