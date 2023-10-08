package com.panda.sport.rcs.oddin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.panda.sport.rcs.oddin.aop.annotation.ValidEnum;
import com.panda.sport.rcs.oddin.enums.EnumMemberType;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 注解校验入参demo dto
 * @Auther: Conway
 * @Date: 2023/08/13 17:33
 */
@Data
public class ValidateParamDto implements Serializable {

    @NotNull(message = "主键Id不能为空")
    private Long id;

    @NotBlank(message = "姓名不能为空")
    @Length(min = 6, max = 20)
    private String name;

    @NotBlank(message = "email不能为空")
    @Email(regexp = ".+[@].+[\\.].+", message = "email格式不对")
    @Length(min = 6, max = 50, message = "email长度范围在6-50之间")
    private String email;

    @NotNull(message = "年龄不能为空")
    @Max(value = 100, message = "年龄超过最大值")
    @Min(value = 18, message = "年龄低于最小值")
    private Integer age;

    @NotBlank(message = "手机不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机格式不对")
    private String mobile;

    @NotNull(message = "会员类型不能为空")
    @ValidEnum(enumClass = EnumMemberType.class, message = "会员类型超出定义范围")
    private Integer memberType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Past(message = "时间格式不对")
    private Date birthday;
}
