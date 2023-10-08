package com.panda.sport.rcs.constants;

import java.util.HashMap;
import java.util.Map;

/***
 * 错误码和错误信息定义类
 * 1. 错误码定义规则为5为数字
 * 2. 前两位表示业务场景，最后三位表示错误码。
 * 例如：10001。10:通用-场景;001:系统未知异常
 * 3. 维护错误码后需要维护错误描述
 * 错误码列表：
 *  10: 通用
 *      001：参数格式校验
 *  11：盘口
 *       001- 查询失败
 *  12: 玩法
 *      001 -查询失败
 *  13: 投注项目
 *      001 --查询失败
 *  .
 *  .
 *  .
 *  90:RPC远程调用
 *      001 --调用失败
 *      002 --网络超时
 *       .
 *       .
 *       .
 * @author kane
 * @since 2019-09-11
 * @version v1.1
 */
public class RcsErrorInfoConstants  {
    /**
     * 错误码与错误描述
     */
    protected static Map<Integer,String> errorMap = new HashMap<>();

    /**
     * 定义系统级异常信息
     */
    private static final String SUCCESS_MESSAGE="成功";
    private static final String SYS_UNKNOWN_EXCEPTION_MESSAGE="系统未知异常";
    private static final String SYS_REQUEST_PARAM_VALIDATE_ERROR_MESSAGE="请求参数有误";
    private static final String SYS_RESPONSE_PARAMS_VALIDATE_ERROR_MESSAGE="请求参数转换响应参数异常";
    /**
     * 成功
     */
    public static final int SUCCESS=10000;

    /***********************************************以10开头COMMON START****************************************************************/
    /**
     *系统未知异常
     */
    public static final int FAIL=10001;
    /**
     * 参数异常
     * **/
    public static final int PARAM_VALIDATE_EXCEPTION = 10002;
    /**
     * 请求参数转换响应参数异常
     * **/
    public static final int REQUEST_TO_RESPONSE_EXCEPTION = 10003;
    static{
        /**
         * 初始化系统预定义的异常码与异常信息
         */
        errorMap.put(SUCCESS, SUCCESS_MESSAGE);
        errorMap.put(FAIL, SYS_UNKNOWN_EXCEPTION_MESSAGE);
        errorMap.put(PARAM_VALIDATE_EXCEPTION, SYS_REQUEST_PARAM_VALIDATE_ERROR_MESSAGE);
        errorMap.put(REQUEST_TO_RESPONSE_EXCEPTION, SYS_RESPONSE_PARAMS_VALIDATE_ERROR_MESSAGE);
    }

    /**
     * 根据异常码获取到系统预设定的异常信息
     * @param errorCode
     * @return
     */
    public static String getSysErrorInfo(int errorCode) {
        return errorMap.get(errorCode);
    }
    /**
     * 构造方法私有化
     */
    private RcsErrorInfoConstants(){}



}
