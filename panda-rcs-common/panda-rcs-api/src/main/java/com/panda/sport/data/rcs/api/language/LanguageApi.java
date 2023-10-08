package com.panda.sport.data.rcs.api.language;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.Map;

/**
 * 国际化api
 */
public interface LanguageApi {

    /**
     * 国际化
     * @param request nameCode
     * @return 国际化
     */
    @POST
    @Path("/getInternationLanguage")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<String> getInternationLanguage(Request<String> request);

    /**
     * 批量得到国际化国际化map
     * @param request nameCodes
     * @return 国际化Maps
     */
    @POST
    @Path("/getInternationLanguageMap")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<Map<String,String>> getInternationLanguageMap(Request<List<String>> request);
}
