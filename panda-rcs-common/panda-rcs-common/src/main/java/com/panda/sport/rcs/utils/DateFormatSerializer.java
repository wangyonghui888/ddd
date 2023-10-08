package com.panda.sport.rcs.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.utils
 * @Description :  date类型转成yyyy-MM-dd HH:mm:ss
 * @Date: 2020-05-15 15:02
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class DateFormatSerializer extends JsonSerializer<Date> {
    @Override
    public void serialize(Date dateVal, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        if (dateVal != null) {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatVal = sf.format(dateVal);
            if (formatVal.endsWith(" 00:00:00")) {
                formatVal = formatVal.substring(0, formatVal.length() - 9);
            }
            jsonGenerator.writeString(formatVal);
        }
    }
}
