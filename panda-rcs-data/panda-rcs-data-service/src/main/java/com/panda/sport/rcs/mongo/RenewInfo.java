package com.panda.sport.rcs.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mongo
 * @Description :  TODO
 * @Date: 2019-10-28 13:41
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Document(collection = "renew_info")
@Data
public class RenewInfo implements Serializable {

    @Field(value = "id")
    private Long id;

    private String tableName;

    private long updateTime;

}
