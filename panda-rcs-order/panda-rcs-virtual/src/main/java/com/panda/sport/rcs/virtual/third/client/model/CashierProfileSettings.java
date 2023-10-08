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


package com.panda.sport.rcs.virtual.third.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.panda.sport.rcs.virtual.third.client.model.CashierProfileGameSettings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO 
 */
@ApiModel(description = "TODO ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class CashierProfileSettings {
  @SerializedName("disablePrintingBetTickets")
  private Boolean disablePrintingBetTickets = null;

  @SerializedName("reprint")
  private Boolean reprint = null;

  @SerializedName("canPrintPayout")
  private Boolean canPrintPayout = null;

  @SerializedName("printPayoutConfirm")
  private Boolean printPayoutConfirm = null;

  @SerializedName("printTicketAndCopy")
  private Boolean printTicketAndCopy = null;

  @SerializedName("blockMaxPayoutTicket")
  private Boolean blockMaxPayoutTicket = null;

  @SerializedName("cancelConfirm")
  private Boolean cancelConfirm = null;

  @SerializedName("canPrintCancel")
  private Boolean canPrintCancel = null;

  @SerializedName("printCancelConfirm")
  private Boolean printCancelConfirm = null;

  @SerializedName("splitStake")
  private Boolean splitStake = null;

  @SerializedName("showJackpot")
  private Boolean showJackpot = null;

  @SerializedName("startFullScreen")
  private Boolean startFullScreen = null;

  @SerializedName("hideNotificationTopBar")
  private Boolean hideNotificationTopBar = null;

  @SerializedName("hideCreditArea")
  private Boolean hideCreditArea = null;

  @SerializedName("hideUsernameArea")
  private Boolean hideUsernameArea = null;

  @SerializedName("hideOptionMenuButton")
  private Boolean hideOptionMenuButton = null;

  @SerializedName("hideRules")
  private Boolean hideRules = null;

  @SerializedName("inactivityTimeout")
  private Integer inactivityTimeout = null;

  @SerializedName("duplicateTips")
  private Boolean duplicateTips = null;

  @SerializedName("gameSettings")
  private List<CashierProfileGameSettings> gameSettings = null;

  public CashierProfileSettings disablePrintingBetTickets(Boolean disablePrintingBetTickets) {
    this.disablePrintingBetTickets = disablePrintingBetTickets;
    return this;
  }

   /**
   * Allow cashier to not print bet tickets.    
   * @return disablePrintingBetTickets
  **/
  @ApiModelProperty(value = "Allow cashier to not print bet tickets.    ")
  public Boolean isDisablePrintingBetTickets() {
    return disablePrintingBetTickets;
  }

  public void setDisablePrintingBetTickets(Boolean disablePrintingBetTickets) {
    this.disablePrintingBetTickets = disablePrintingBetTickets;
  }

  public CashierProfileSettings reprint(Boolean reprint) {
    this.reprint = reprint;
    return this;
  }

   /**
   * Allow to reprint tickets on demand. 
   * @return reprint
  **/
  @ApiModelProperty(value = "Allow to reprint tickets on demand. ")
  public Boolean isReprint() {
    return reprint;
  }

  public void setReprint(Boolean reprint) {
    this.reprint = reprint;
  }

  public CashierProfileSettings canPrintPayout(Boolean canPrintPayout) {
    this.canPrintPayout = canPrintPayout;
    return this;
  }

   /**
   * Allow cashier to print payout tickets 
   * @return canPrintPayout
  **/
  @ApiModelProperty(value = "Allow cashier to print payout tickets ")
  public Boolean isCanPrintPayout() {
    return canPrintPayout;
  }

  public void setCanPrintPayout(Boolean canPrintPayout) {
    this.canPrintPayout = canPrintPayout;
  }

  public CashierProfileSettings printPayoutConfirm(Boolean printPayoutConfirm) {
    this.printPayoutConfirm = printPayoutConfirm;
    return this;
  }

   /**
   * Force confirmation of ticket payout. 
   * @return printPayoutConfirm
  **/
  @ApiModelProperty(value = "Force confirmation of ticket payout. ")
  public Boolean isPrintPayoutConfirm() {
    return printPayoutConfirm;
  }

  public void setPrintPayoutConfirm(Boolean printPayoutConfirm) {
    this.printPayoutConfirm = printPayoutConfirm;
  }

  public CashierProfileSettings printTicketAndCopy(Boolean printTicketAndCopy) {
    this.printTicketAndCopy = printTicketAndCopy;
    return this;
  }

   /**
   * Print receipt and copy at the same time. 
   * @return printTicketAndCopy
  **/
  @ApiModelProperty(value = "Print receipt and copy at the same time. ")
  public Boolean isPrintTicketAndCopy() {
    return printTicketAndCopy;
  }

  public void setPrintTicketAndCopy(Boolean printTicketAndCopy) {
    this.printTicketAndCopy = printTicketAndCopy;
  }

  public CashierProfileSettings blockMaxPayoutTicket(Boolean blockMaxPayoutTicket) {
    this.blockMaxPayoutTicket = blockMaxPayoutTicket;
    return this;
  }

   /**
   * If true, not print ticket if exceeds max payout 
   * @return blockMaxPayoutTicket
  **/
  @ApiModelProperty(value = "If true, not print ticket if exceeds max payout ")
  public Boolean isBlockMaxPayoutTicket() {
    return blockMaxPayoutTicket;
  }

  public void setBlockMaxPayoutTicket(Boolean blockMaxPayoutTicket) {
    this.blockMaxPayoutTicket = blockMaxPayoutTicket;
  }

  public CashierProfileSettings cancelConfirm(Boolean cancelConfirm) {
    this.cancelConfirm = cancelConfirm;
    return this;
  }

   /**
   * Force confirmation to cancel ticket. 
   * @return cancelConfirm
  **/
  @ApiModelProperty(value = "Force confirmation to cancel ticket. ")
  public Boolean isCancelConfirm() {
    return cancelConfirm;
  }

  public void setCancelConfirm(Boolean cancelConfirm) {
    this.cancelConfirm = cancelConfirm;
  }

  public CashierProfileSettings canPrintCancel(Boolean canPrintCancel) {
    this.canPrintCancel = canPrintCancel;
    return this;
  }

   /**
   * Allow cashier to print cancel ticket. 
   * @return canPrintCancel
  **/
  @ApiModelProperty(value = "Allow cashier to print cancel ticket. ")
  public Boolean isCanPrintCancel() {
    return canPrintCancel;
  }

  public void setCanPrintCancel(Boolean canPrintCancel) {
    this.canPrintCancel = canPrintCancel;
  }

  public CashierProfileSettings printCancelConfirm(Boolean printCancelConfirm) {
    this.printCancelConfirm = printCancelConfirm;
    return this;
  }

   /**
   * Force to show confirmation to print cancel ticket. 
   * @return printCancelConfirm
  **/
  @ApiModelProperty(value = "Force to show confirmation to print cancel ticket. ")
  public Boolean isPrintCancelConfirm() {
    return printCancelConfirm;
  }

  public void setPrintCancelConfirm(Boolean printCancelConfirm) {
    this.printCancelConfirm = printCancelConfirm;
  }

  public CashierProfileSettings splitStake(Boolean splitStake) {
    this.splitStake = splitStake;
    return this;
  }

   /**
   * Split ticket tip stake on single bet mode. 
   * @return splitStake
  **/
  @ApiModelProperty(value = "Split ticket tip stake on single bet mode. ")
  public Boolean isSplitStake() {
    return splitStake;
  }

  public void setSplitStake(Boolean splitStake) {
    this.splitStake = splitStake;
  }

  public CashierProfileSettings showJackpot(Boolean showJackpot) {
    this.showJackpot = showJackpot;
    return this;
  }

   /**
   * Show jackpot data when check a won bet ticket 
   * @return showJackpot
  **/
  @ApiModelProperty(value = "Show jackpot data when check a won bet ticket ")
  public Boolean isShowJackpot() {
    return showJackpot;
  }

  public void setShowJackpot(Boolean showJackpot) {
    this.showJackpot = showJackpot;
  }

  public CashierProfileSettings startFullScreen(Boolean startFullScreen) {
    this.startFullScreen = startFullScreen;
    return this;
  }

   /**
   * Start cashier in full screen mode            
   * @return startFullScreen
  **/
  @ApiModelProperty(value = "Start cashier in full screen mode            ")
  public Boolean isStartFullScreen() {
    return startFullScreen;
  }

  public void setStartFullScreen(Boolean startFullScreen) {
    this.startFullScreen = startFullScreen;
  }

  public CashierProfileSettings hideNotificationTopBar(Boolean hideNotificationTopBar) {
    this.hideNotificationTopBar = hideNotificationTopBar;
    return this;
  }

   /**
   * Hide the Notification top bar at the Cashier betting interface. The header line inside the widget over the top app bar 
   * @return hideNotificationTopBar
  **/
  @ApiModelProperty(value = "Hide the Notification top bar at the Cashier betting interface. The header line inside the widget over the top app bar ")
  public Boolean isHideNotificationTopBar() {
    return hideNotificationTopBar;
  }

  public void setHideNotificationTopBar(Boolean hideNotificationTopBar) {
    this.hideNotificationTopBar = hideNotificationTopBar;
  }

  public CashierProfileSettings hideCreditArea(Boolean hideCreditArea) {
    this.hideCreditArea = hideCreditArea;
    return this;
  }

   /**
   * Hide the Credit area (Balance value) over the Betslip at the Cashier betting interface 
   * @return hideCreditArea
  **/
  @ApiModelProperty(value = "Hide the Credit area (Balance value) over the Betslip at the Cashier betting interface ")
  public Boolean isHideCreditArea() {
    return hideCreditArea;
  }

  public void setHideCreditArea(Boolean hideCreditArea) {
    this.hideCreditArea = hideCreditArea;
  }

  public CashierProfileSettings hideUsernameArea(Boolean hideUsernameArea) {
    this.hideUsernameArea = hideUsernameArea;
    return this;
  }

   /**
   * Hide the Username area over the Betslip of the Cashier betting interface 
   * @return hideUsernameArea
  **/
  @ApiModelProperty(value = "Hide the Username area over the Betslip of the Cashier betting interface ")
  public Boolean isHideUsernameArea() {
    return hideUsernameArea;
  }

  public void setHideUsernameArea(Boolean hideUsernameArea) {
    this.hideUsernameArea = hideUsernameArea;
  }

  public CashierProfileSettings hideOptionMenuButton(Boolean hideOptionMenuButton) {
    this.hideOptionMenuButton = hideOptionMenuButton;
    return this;
  }

   /**
   * Hide the Option Menu button over the Betslip at the Cashier betting interface 
   * @return hideOptionMenuButton
  **/
  @ApiModelProperty(value = "Hide the Option Menu button over the Betslip at the Cashier betting interface ")
  public Boolean isHideOptionMenuButton() {
    return hideOptionMenuButton;
  }

  public void setHideOptionMenuButton(Boolean hideOptionMenuButton) {
    this.hideOptionMenuButton = hideOptionMenuButton;
  }

  public CashierProfileSettings hideRules(Boolean hideRules) {
    this.hideRules = hideRules;
    return this;
  }

   /**
   * Enable parameter to hide the \&quot;Game rules\&quot; button in the Cashier betting interface            
   * @return hideRules
  **/
  @ApiModelProperty(value = "Enable parameter to hide the \"Game rules\" button in the Cashier betting interface            ")
  public Boolean isHideRules() {
    return hideRules;
  }

  public void setHideRules(Boolean hideRules) {
    this.hideRules = hideRules;
  }

  public CashierProfileSettings inactivityTimeout(Integer inactivityTimeout) {
    this.inactivityTimeout = inactivityTimeout;
    return this;
  }

   /**
   * Time in SECONDS from which cashier closes session when there is no activity. 0 when never close session. 
   * minimum: 0
   * @return inactivityTimeout
  **/
  @ApiModelProperty(value = "Time in SECONDS from which cashier closes session when there is no activity. 0 when never close session. ")
  public Integer getInactivityTimeout() {
    return inactivityTimeout;
  }

  public void setInactivityTimeout(Integer inactivityTimeout) {
    this.inactivityTimeout = inactivityTimeout;
  }

  public CashierProfileSettings duplicateTips(Boolean duplicateTips) {
    this.duplicateTips = duplicateTips;
    return this;
  }

   /**
   * Do Not Allow Duplicate Tips. 
   * @return duplicateTips
  **/
  @ApiModelProperty(value = "Do Not Allow Duplicate Tips. ")
  public Boolean isDuplicateTips() {
    return duplicateTips;
  }

  public void setDuplicateTips(Boolean duplicateTips) {
    this.duplicateTips = duplicateTips;
  }

  public CashierProfileSettings gameSettings(List<CashierProfileGameSettings> gameSettings) {
    this.gameSettings = gameSettings;
    return this;
  }

  public CashierProfileSettings addGameSettingsItem(CashierProfileGameSettings gameSettingsItem) {
    if (this.gameSettings == null) {
      this.gameSettings = new ArrayList<CashierProfileGameSettings>();
    }
    this.gameSettings.add(gameSettingsItem);
    return this;
  }

   /**
   * Specific cashier profile settings per game 
   * @return gameSettings
  **/
  @ApiModelProperty(value = "Specific cashier profile settings per game ")
  public List<CashierProfileGameSettings> getGameSettings() {
    return gameSettings;
  }

  public void setGameSettings(List<CashierProfileGameSettings> gameSettings) {
    this.gameSettings = gameSettings;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CashierProfileSettings cashierProfileSettings = (CashierProfileSettings) o;
    return Objects.equals(this.disablePrintingBetTickets, cashierProfileSettings.disablePrintingBetTickets) &&
        Objects.equals(this.reprint, cashierProfileSettings.reprint) &&
        Objects.equals(this.canPrintPayout, cashierProfileSettings.canPrintPayout) &&
        Objects.equals(this.printPayoutConfirm, cashierProfileSettings.printPayoutConfirm) &&
        Objects.equals(this.printTicketAndCopy, cashierProfileSettings.printTicketAndCopy) &&
        Objects.equals(this.blockMaxPayoutTicket, cashierProfileSettings.blockMaxPayoutTicket) &&
        Objects.equals(this.cancelConfirm, cashierProfileSettings.cancelConfirm) &&
        Objects.equals(this.canPrintCancel, cashierProfileSettings.canPrintCancel) &&
        Objects.equals(this.printCancelConfirm, cashierProfileSettings.printCancelConfirm) &&
        Objects.equals(this.splitStake, cashierProfileSettings.splitStake) &&
        Objects.equals(this.showJackpot, cashierProfileSettings.showJackpot) &&
        Objects.equals(this.startFullScreen, cashierProfileSettings.startFullScreen) &&
        Objects.equals(this.hideNotificationTopBar, cashierProfileSettings.hideNotificationTopBar) &&
        Objects.equals(this.hideCreditArea, cashierProfileSettings.hideCreditArea) &&
        Objects.equals(this.hideUsernameArea, cashierProfileSettings.hideUsernameArea) &&
        Objects.equals(this.hideOptionMenuButton, cashierProfileSettings.hideOptionMenuButton) &&
        Objects.equals(this.hideRules, cashierProfileSettings.hideRules) &&
        Objects.equals(this.inactivityTimeout, cashierProfileSettings.inactivityTimeout) &&
        Objects.equals(this.duplicateTips, cashierProfileSettings.duplicateTips) &&
        Objects.equals(this.gameSettings, cashierProfileSettings.gameSettings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(disablePrintingBetTickets, reprint, canPrintPayout, printPayoutConfirm, printTicketAndCopy, blockMaxPayoutTicket, cancelConfirm, canPrintCancel, printCancelConfirm, splitStake, showJackpot, startFullScreen, hideNotificationTopBar, hideCreditArea, hideUsernameArea, hideOptionMenuButton, hideRules, inactivityTimeout, duplicateTips, gameSettings);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CashierProfileSettings {\n");
    
    sb.append("    disablePrintingBetTickets: ").append(toIndentedString(disablePrintingBetTickets)).append("\n");
    sb.append("    reprint: ").append(toIndentedString(reprint)).append("\n");
    sb.append("    canPrintPayout: ").append(toIndentedString(canPrintPayout)).append("\n");
    sb.append("    printPayoutConfirm: ").append(toIndentedString(printPayoutConfirm)).append("\n");
    sb.append("    printTicketAndCopy: ").append(toIndentedString(printTicketAndCopy)).append("\n");
    sb.append("    blockMaxPayoutTicket: ").append(toIndentedString(blockMaxPayoutTicket)).append("\n");
    sb.append("    cancelConfirm: ").append(toIndentedString(cancelConfirm)).append("\n");
    sb.append("    canPrintCancel: ").append(toIndentedString(canPrintCancel)).append("\n");
    sb.append("    printCancelConfirm: ").append(toIndentedString(printCancelConfirm)).append("\n");
    sb.append("    splitStake: ").append(toIndentedString(splitStake)).append("\n");
    sb.append("    showJackpot: ").append(toIndentedString(showJackpot)).append("\n");
    sb.append("    startFullScreen: ").append(toIndentedString(startFullScreen)).append("\n");
    sb.append("    hideNotificationTopBar: ").append(toIndentedString(hideNotificationTopBar)).append("\n");
    sb.append("    hideCreditArea: ").append(toIndentedString(hideCreditArea)).append("\n");
    sb.append("    hideUsernameArea: ").append(toIndentedString(hideUsernameArea)).append("\n");
    sb.append("    hideOptionMenuButton: ").append(toIndentedString(hideOptionMenuButton)).append("\n");
    sb.append("    hideRules: ").append(toIndentedString(hideRules)).append("\n");
    sb.append("    inactivityTimeout: ").append(toIndentedString(inactivityTimeout)).append("\n");
    sb.append("    duplicateTips: ").append(toIndentedString(duplicateTips)).append("\n");
    sb.append("    gameSettings: ").append(toIndentedString(gameSettings)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

