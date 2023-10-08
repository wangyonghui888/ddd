package com.panda.sport.rcs.third.entity.betguard.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Beulah
 * @date 2023/3/29 19:08
 * @description GetClientDetails 响应实体
 */

@Data
public class GetClientDetailsVo implements Serializable {

    private static final long serialVersionUID = -1606664692753764046L;

    // PM后端用於登錄的用戶登錄。（必填）
    private String Login;
    //用戶的貨幣 ID。 請參閱有效的貨幣代碼值。（CNY）（必填）
    private String CurrencyId;
    // Unique ID of User in PM后端 （必填）
    private String ExternalId;

    //错误码
    private String ErrorCode = "0";
    //错误描述
    private String ErrorText = "";


    /*// 用戶語言的兩個字母代碼。 例如“en”——英語，“ru”——俄語等。有關有效值，請參閱語言代碼。Zh: Chinese
    private String LanguageId;
    //邮箱
    private String Email;
    //生日
    private Date BirthDate;
    //1 – for Male, 2 – for Female.
    private int Gender;
    //ip
    private String CurrentIp;
    //电话
    private String Phone;
    //地址
    private String Address;
    // 當前分配給 PM后端 的唯一獎勵 ID給用戶。 如果 PM 不使用，則可以省略獎金，或用戶沒有當前獎金。 查看獎金API 了解更多詳情
    private int ExternalBonusId;
    //是否试玩用户
    private Boolean IsTest;
    //注册国籍
    private String CountryId;
    // 可選的標誌字段，用於區分某些客戶端和其他客戶端
    private String PartnerFlag;*/


}
