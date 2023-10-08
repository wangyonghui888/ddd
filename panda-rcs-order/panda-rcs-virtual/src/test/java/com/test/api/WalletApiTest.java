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

import com.panda.sport.rcs.virtual.third.client.model.Transaction;
import com.panda.sport.rcs.virtual.third.client.model.Wallet;
import org.junit.Ignore;
import org.junit.Test;
import org.threeten.bp.OffsetDateTime;

import java.util.List;

/**
 * API tests for WalletApi
 */
@Ignore
public class WalletApiTest {

    private final com.panda.sport.rcs.virtual.third.client.api.WalletApi api = new com.panda.sport.rcs.virtual.third.client.api.WalletApi();

    
    /**
     * 
     *
     * Creation of a new wallet, whose currency (currencyIso), its external id (extId), the external information (extData), boolean for is a promotion (isPromotion) and the status of the wallet. - the external identifier (*extId*) - a JSON object that is used as Information repository on body request (*extData*) - the status of the entity (enabled, disabled or deleted). 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void walletCreateTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        ApiTest.setApiDomain();

        Integer entityId = 3936;
        String currency = "RMB";
        String extId = null;
        String extData = null;
        Double balance = null;
        Integer priority = null;
        Boolean isPromotion = null;
        String description = null;
        OffsetDateTime startDate = null;
        OffsetDateTime endDate = null;
        com.panda.sport.rcs.virtual.third.client.model.Wallet response = api.walletCreate(entityId, currency, extId, extData, balance, priority, isPromotion, description, startDate, endDate);
        System.out.println(response);
        // TODO: test validations
    }
    
    /**
     * 
     *
     * Increments credit as a transaction on current unit. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void walletCreditAddTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer walletId = null;
        String currencyCode = null;
        Double amount = null;
        String extTransactionId = null;
        String extData = null;
        String method = null;
        com.panda.sport.rcs.virtual.third.client.model.Transaction response = api.walletCreditAdd(walletId, currencyCode, amount, extTransactionId, extData, method);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * Clears all the credit as a transaction on currrent unit. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void walletCreditClearTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer walletId = null;
        String currencyCode = null;
        String extTransactionId = null;
        String extData = null;
        String method = null;
        com.panda.sport.rcs.virtual.third.client.model.Transaction response = api.walletCreditClear(walletId, currencyCode, extTransactionId, extData, method);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * Produces a history of credit transactions
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void walletCreditFindTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer entityId = null;
        OffsetDateTime startTime = null;
        Integer n = null;
        Integer first = null;
        String orderBy = null;
        Integer walletId = null;
        OffsetDateTime endTime = null;
        String currency = null;
        Integer operatorId = null;
        String operatorAPI = null;
        String method = null;
        Boolean withChildren = null;
        List<Transaction> response = api.walletCreditFind(entityId, startTime, n, first, orderBy, walletId, endTime, currency, operatorId, operatorAPI, method, withChildren);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * Return transaction list
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void walletCreditFindByIdTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer entityId = null;
        Integer ticketId = null;
        Integer transactionId = null;
        String extTransactionId = null;
        Boolean withChildren = null;
        List<Transaction> response = api.walletCreditFindById(entityId, ticketId, transactionId, extTransactionId, withChildren);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * Decrements credit as a transaction on currrent unit. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void walletCreditRemoveTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer walletId = null;
        String currencyCode = null;
        Double amount = null;
        String extTransactionId = null;
        String extData = null;
        String method = null;
        com.panda.sport.rcs.virtual.third.client.model.Transaction response = api.walletCreditRemove(walletId, currencyCode, amount, extTransactionId, extData, method);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * To set an amount as the new credit value; this will override any limit or check. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void walletCreditSetTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer walletId = null;
        String currencyCode = null;
        Double amount = null;
        String extTransactionId = null;
        String extData = null;
        String method = null;
        com.panda.sport.rcs.virtual.third.client.model.Transaction response = api.walletCreditSet(walletId, currencyCode, amount, extTransactionId, extData, method);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * Transfer credit from one wallet to another. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void walletCreditTransferTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer walletIdFrom = null;
        Integer walletIdTo = null;
        String currencyCode = "RMB";
        Double amount = null;
        String extTransactionId = null;
        String extData = null;
        String method = null;
        List<Transaction> response = api.walletCreditTransfer(walletIdFrom, walletIdTo, currencyCode, amount, extTransactionId, extData, method);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * Return all the wallet availables by entity id, hierarchically.  
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void walletFindAllByEntityIdTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        ApiTest.setApiDomain();

        Integer entityId = 3936;
        Integer n = 10;
        Integer first = 10;
        String orderBy = "DESC";
        Boolean withChildren = null;
        List<Wallet> response = api.walletFindAllByEntityId(entityId, n, first, orderBy, withChildren);
        System.out.println(response);
        // TODO: test validations
    }
    
    /**
     * 
     *
     * Return a wallet availables by wallet id. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void walletFindByIdTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer entityId = null;
        Integer walletId = null;
        String extId = null;
        Boolean withChildren = null;
        List<Wallet> response = api.walletFindById(entityId, walletId, extId, withChildren);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void walletRetryCancelTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        List<Long> ticketIds = null;
        List<Object> response = api.walletRetryCancel(ticketIds);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void walletRetryPayoutTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        List<Long> ticketIds = null;
        List<Object> response = api.walletRetryPayout(ticketIds);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void walletRetrySellTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        List<Long> ticketIds = null;
        List<Object> response = api.walletRetrySell(ticketIds);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void walletRetrySolveTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        List<Long> ticketIds = null;
        List<Object> response = api.walletRetrySolve(ticketIds);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * Edition of a new wallet, whose currency (currencyIso), its external id (extId), the external information (extData), boolean for is a promotion (isPromotion) and the status of the wallet. - the external identifier (*extId*) - a JSON object that is used as Information repository on body request (*extData*) - the status of the entity (enabled, disabled or deleted). 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void walletSetTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer walletId = null;
        String status = null;
        String extId = null;
        String extData = null;
        Integer priority = null;
        String description = null;
        OffsetDateTime startDate = null;
        OffsetDateTime endDate = null;
        com.panda.sport.rcs.virtual.third.client.model.Wallet response = api.walletSet(walletId, status, extId, extData, priority, description, startDate, endDate);

        // TODO: test validations
    }
    
}
