package com.hospital.reserve.common.exception;

import com.hospital.reserve.common.result.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 自定义全局异常类
 *
 * @author qy
 */
@Data
@ApiModel(value = "自定义全局异常类")
public class HospitalException extends RuntimeException {

    @ApiModelProperty(value = "invalid code")
    private Integer code;

    /**

     * @param message
     * @param code
     */
    public HospitalException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    /**

     * @param resultCodeEnum
     */
    public HospitalException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "HospitalException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
