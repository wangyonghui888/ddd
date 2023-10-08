package com.panda.sport.rcs.common.bean;

import com.panda.sport.rcs.common.constants.Constants;
import com.panda.sport.rcs.common.utils.CommonUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 返回信息
 *
 * @param <T>
 * @author lithan
 */
@ApiModel(value = "通用API返回结果", description = "规则管理表")
public class Result<T> {

    @ApiModelProperty(value = "编码")
    public int code;

    @ApiModelProperty(value = "描述")
    public String msg;

    @ApiModelProperty(value = "数据")
    public T data;

    @ApiModelProperty(value = "linkId")
    public String linkId;

    //扩展字段 仅code=200时为true
    @ApiModelProperty(value = "")
    public boolean success;

    public Result(){
        this.linkId = CommonUtils.getTradeIdByMdc();
    }
    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.setCode(Constants.FAIL_CODE);
        result.setMsg(Constants.FAIL_MSG);
        return result;
    }

    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(Constants.FAIL_CODE);
        result.setMsg(message);
        return result;
    }

    public static <T> Result<T> succes() {
        Result<T> result = new Result<>();
        result.setCode(Constants.SUCCESS_CODE);
        result.setMsg(Constants.SUCCESS_MSG);
        result.setSuccess(true);
        return result;
    }

    public static <T> Result<T> succes(T data) {
        Result<T> result = new Result<>();
        result.setCode(Constants.SUCCESS_CODE);
        result.setMsg(Constants.SUCCESS_MSG);
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
