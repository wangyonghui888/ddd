/*
 * GoldenRace External API
 * Definitions of External API for GoldenRace Java Server 
 *
 * OpenAPI spec version: 7.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.test.api;

import com.panda.sport.rcs.virtual.third.client.model.Stat;
import com.panda.sport.rcs.virtual.third.client.model.StatDetail;
import org.junit.Ignore;
import org.junit.Test;
import org.threeten.bp.OffsetDateTime;

import java.util.List;

/**
 * API tests for StatsApi
 */
@Ignore
public class StatsApiTest {

    private final com.panda.sport.rcs.virtual.third.client.api.StatsApi api = new com.panda.sport.rcs.virtual.third.client.api.StatsApi();

    
    /**
     * 
     *
     * This method returns accumulated totals for earnings between two time points. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void statsEarningTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer entityId = null;
        OffsetDateTime startTime = null;
        String timeLevel = null;
        OffsetDateTime endTime = null;
        String entityLevel = null;
        Boolean groupByDate = null;
        String tags = null;
        List<Stat> response = api.statsEarning(entityId, startTime, timeLevel, endTime, entityLevel, groupByDate, tags);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * This method returns accumulated totals for earnings between two time points. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void statsEarningDetailsTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer entityId = null;
        OffsetDateTime startTime = null;
        String timeLevel = null;
        OffsetDateTime endTime = null;
        List<String> marketLevel = null;
        String entityLevel = null;
        Boolean groupByDate = null;
        List<String> gameFilter = null;
        List<String> playlistFilter = null;
        String tags = null;
        List<StatDetail> response = api.statsEarningDetails(entityId, startTime, timeLevel, endTime, marketLevel, entityLevel, groupByDate, gameFilter, playlistFilter, tags);

        // TODO: test validations
    }
    
}
