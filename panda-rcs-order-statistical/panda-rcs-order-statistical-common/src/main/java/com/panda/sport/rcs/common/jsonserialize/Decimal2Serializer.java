package com.panda.sport.rcs.common.jsonserialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.panda.sport.rcs.common.constants.Constants;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author lithan
 * @description
 * @date 2020/3/13 16:08
 */
public class Decimal2Serializer extends JsonSerializer<BigDecimal> {



    /**
     * 将返回的BigDecimal保留两位小数，再返回给前端
     */
    @Override
    public void serialize(BigDecimal num, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

        if (num == null) {
            num = BigDecimal.ZERO;
        }
        if (new BigDecimal(num.intValue()).compareTo(num) == 0) {
            num = num.setScale(0, BigDecimal.ROUND_DOWN);
        } else {
            num = num.setScale(Constants.PRECISION, BigDecimal.ROUND_DOWN);
        }
        jsonGenerator.writeString(num.toString());
    }
}