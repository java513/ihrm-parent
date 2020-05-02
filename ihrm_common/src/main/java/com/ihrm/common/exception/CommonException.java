package com.ihrm.common.exception;

import com.ihrm.common.entity.ResultCode;
import lombok.Data;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/22 12:47
 */
@Data
public class CommonException extends Exception{
    private ResultCode resultCode;
    private boolean success;//是否成功
    private Integer code;// 返回码
    private String message;//返回信息
    public CommonException(){

    }
    public CommonException(ResultCode resultCode){
        this.resultCode=resultCode;
    }

    public CommonException(boolean success,Integer code,String message) {
        this.code = code;
        this.message = message;
        this.success = success;
    }
}
