package com.panda.sport.rcs.oddin.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Beulah
 * @date 2023/3/29 19:22
 * @description todo
 */

@Getter
@AllArgsConstructor
public enum BetGuardErrorEnum {

    BetAmountError("BetAmountError", "Bet Amount Error (Cashout)"),
    BetNotFoundError("BetNotFoundError", "The bet is not found"),
    BetSelectionChanged("BetSelectionChanged", "Odds have been changed"),
    BetSelectionsCanNotBeNullOrEmpty("BetSelectionsCanNotBeNullOrEmpty", "Bet Selections Can Not Be Null Or Empty"),
    BetSelectionsCombindedError("BetSelectionsCombindedError", "Please change Selections combination of your betslip"),
    BetStateError("BetStateError", "Bet State Error (Cashout)"),
    BetTypeError("BetTypeError", "Bet Type Error"),
    ClientBetMinStakeLimitError("ClientBetMinStakeLimitError", "The bet stake is lower than minimum allowed"),
    ClientBetStakeLimitError("ClientBetStakeLimitError", "The bet stake is greater than maximum allowed"),
    ClientLocked("ClientLocked", "Client Locked"),
    ClientRestrictedForAction("ClientRestrictedForAction", "Client Restricted For Action"),
    EachWayIsNotAvailable("EachWayIsNotAvailable", "Each Way Is Not Available"),
    InternalError("InternalError", "Internal error"),
    InvalidCategory("InvalidCategory", "Invalid Category"),
    InvalidParameters("InvalidParameters", "Invalid Parameters"),
    InvalidPartner("InvalidPartner", "Invalid Partner"),
    LinkedMatches("LinkedMatches", "Linked (not combinable) matches"),
    MarketNotFound("MarketNotFound", "Market Not Found"),
    MarketNotVisible("MarketNotVisible", "Market Not Visible"),
    MarketSuspendedMa("MarketSuspended", "Market suspended"),
    MatchNotBooked("MatchNotBooked", "Match Not Booked"),
    MatchNotFound("MatchNotFound", "Match not found"),
    MatchNotVisible("MatchNotVisible", "Match Not Visible"),
    MatchSuspended("MatchSuspended", "Match Suspended"),
    MaxSingleBetAmountError("MaxSingleBetAmountError", "Single bet amount error"),
    NotAuthorized("NotAuthorized", "Not authorized"),
    NotSupportedCurrency("NotSupportedCurrency", "Not Supported Currency"),
    OperationInProgress("OperationInProgress", "Operation In Progress"),
    PartnerApiError("PartnerApiError", "PartnerApiError"),
    PartnerApiSecretKeyMissing("PartnerApiSecretKeyMissing", "Partner Api Secret Key Missing in External Admin"),
    PartnerApiWrongHash("PartnerApiWrongHash", "Wrong hash"),
    PartnerBlocked("PartnerBlocked", "Partner Blocked"),
    PartnerLimitAmountExceed("PartnerLimitAmountExceed", "Partner Limit Amount Exceed"),
    PartnerMismatch("PartnerMismatch", "Partner Mismatch"),
    PartnerNotFound("PartnerNotFound", "Partner Not Found"),
    PriceWasChanged("PriceWasChanged", "Price Was Changed"),
    regionNotFound("regionNotFound", "Region was not found"),
    RequiredFieldsMissing("RequiredFieldsMissing", "Required Fields Missing"),
    SelectionMultipleCount("SelectionMultipleCount", "Selection {0} must be combined with at least {1} other selections"),
    SelectionNotFound("SelectionNotFound", "Selection Not Found"),
    SelectionSinglesOnly("SelectionSinglesOnly", "Selection is Singles only"),
    SelectionSuspended("SelectionSuspended", "Selection suspended"),
    SPMissing("SPMissing", "SP Missing"),
    TokenAlreadyExists("TokenAlreadyExists", "Token Already Exists"),
    WrongCurrencyCode("WrongCurrencyCode", "Wrong Currency Code")
    ;


    private String errorCode;
    private String errorText;
}
