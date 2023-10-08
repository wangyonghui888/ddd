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
import java.io.IOException;

/**
 * TODO 
 */
@ApiModel(description = "TODO ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class ShopadminProfileSettings {
  @SerializedName("showTurnover")
  private Boolean showTurnover = null;

  @SerializedName("showTurnoverStaffName")
  private Boolean showTurnoverStaffName = null;

  @SerializedName("showOpenTickets")
  private Boolean showOpenTickets = null;

  @SerializedName("showTickets")
  private Boolean showTickets = null;

  @SerializedName("showCredit")
  private Boolean showCredit = null;

  @SerializedName("showResults")
  private Boolean showResults = null;

  @SerializedName("showReleaseNotes")
  private Boolean showReleaseNotes = null;

  @SerializedName("hideTicketId")
  private Boolean hideTicketId = null;

  @SerializedName("canCancel")
  private Boolean canCancel = null;

  public ShopadminProfileSettings showTurnover(Boolean showTurnover) {
    this.showTurnover = showTurnover;
    return this;
  }

   /**
   * Show turnover tab on shpadmin 
   * @return showTurnover
  **/
  @ApiModelProperty(value = "Show turnover tab on shpadmin ")
  public Boolean isShowTurnover() {
    return showTurnover;
  }

  public void setShowTurnover(Boolean showTurnover) {
    this.showTurnover = showTurnover;
  }

  public ShopadminProfileSettings showTurnoverStaffName(Boolean showTurnoverStaffName) {
    this.showTurnoverStaffName = showTurnoverStaffName;
    return this;
  }

   /**
   * Show the staff name on turnover tab 
   * @return showTurnoverStaffName
  **/
  @ApiModelProperty(value = "Show the staff name on turnover tab ")
  public Boolean isShowTurnoverStaffName() {
    return showTurnoverStaffName;
  }

  public void setShowTurnoverStaffName(Boolean showTurnoverStaffName) {
    this.showTurnoverStaffName = showTurnoverStaffName;
  }

  public ShopadminProfileSettings showOpenTickets(Boolean showOpenTickets) {
    this.showOpenTickets = showOpenTickets;
    return this;
  }

   /**
   * Show open tickets tab on shopadmin 
   * @return showOpenTickets
  **/
  @ApiModelProperty(value = "Show open tickets tab on shopadmin ")
  public Boolean isShowOpenTickets() {
    return showOpenTickets;
  }

  public void setShowOpenTickets(Boolean showOpenTickets) {
    this.showOpenTickets = showOpenTickets;
  }

  public ShopadminProfileSettings showTickets(Boolean showTickets) {
    this.showTickets = showTickets;
    return this;
  }

   /**
   * Show tickets tab on shopadmin 
   * @return showTickets
  **/
  @ApiModelProperty(value = "Show tickets tab on shopadmin ")
  public Boolean isShowTickets() {
    return showTickets;
  }

  public void setShowTickets(Boolean showTickets) {
    this.showTickets = showTickets;
  }

  public ShopadminProfileSettings showCredit(Boolean showCredit) {
    this.showCredit = showCredit;
    return this;
  }

   /**
   * Show credit tab on shopadmin 
   * @return showCredit
  **/
  @ApiModelProperty(value = "Show credit tab on shopadmin ")
  public Boolean isShowCredit() {
    return showCredit;
  }

  public void setShowCredit(Boolean showCredit) {
    this.showCredit = showCredit;
  }

  public ShopadminProfileSettings showResults(Boolean showResults) {
    this.showResults = showResults;
    return this;
  }

   /**
   * Show Results tab on shopadmin 
   * @return showResults
  **/
  @ApiModelProperty(value = "Show Results tab on shopadmin ")
  public Boolean isShowResults() {
    return showResults;
  }

  public void setShowResults(Boolean showResults) {
    this.showResults = showResults;
  }

  public ShopadminProfileSettings showReleaseNotes(Boolean showReleaseNotes) {
    this.showReleaseNotes = showReleaseNotes;
    return this;
  }

   /**
   * Show Release Notes tab on shopadmin 
   * @return showReleaseNotes
  **/
  @ApiModelProperty(value = "Show Release Notes tab on shopadmin ")
  public Boolean isShowReleaseNotes() {
    return showReleaseNotes;
  }

  public void setShowReleaseNotes(Boolean showReleaseNotes) {
    this.showReleaseNotes = showReleaseNotes;
  }

  public ShopadminProfileSettings hideTicketId(Boolean hideTicketId) {
    this.hideTicketId = hideTicketId;
    return this;
  }

   /**
   * Hide the ticket id on open ticket and tickets tabs 
   * @return hideTicketId
  **/
  @ApiModelProperty(value = "Hide the ticket id on open ticket and tickets tabs ")
  public Boolean isHideTicketId() {
    return hideTicketId;
  }

  public void setHideTicketId(Boolean hideTicketId) {
    this.hideTicketId = hideTicketId;
  }

  public ShopadminProfileSettings canCancel(Boolean canCancel) {
    this.canCancel = canCancel;
    return this;
  }

   /**
   * This option is add or remove the \&quot;Cancel\&quot; button within the ShopAdmin (Menu &gt; Admin &gt; Open Tickets page of the cashier). If ON, the button will show on this page, allowing tickets to be cancelled before the event starts. If OFF, tickets cannot be cancelled from the Open Tickets page of ShopAdmin. 
   * @return canCancel
  **/
  @ApiModelProperty(value = "This option is add or remove the \"Cancel\" button within the ShopAdmin (Menu > Admin > Open Tickets page of the cashier). If ON, the button will show on this page, allowing tickets to be cancelled before the event starts. If OFF, tickets cannot be cancelled from the Open Tickets page of ShopAdmin. ")
  public Boolean isCanCancel() {
    return canCancel;
  }

  public void setCanCancel(Boolean canCancel) {
    this.canCancel = canCancel;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ShopadminProfileSettings shopadminProfileSettings = (ShopadminProfileSettings) o;
    return Objects.equals(this.showTurnover, shopadminProfileSettings.showTurnover) &&
        Objects.equals(this.showTurnoverStaffName, shopadminProfileSettings.showTurnoverStaffName) &&
        Objects.equals(this.showOpenTickets, shopadminProfileSettings.showOpenTickets) &&
        Objects.equals(this.showTickets, shopadminProfileSettings.showTickets) &&
        Objects.equals(this.showCredit, shopadminProfileSettings.showCredit) &&
        Objects.equals(this.showResults, shopadminProfileSettings.showResults) &&
        Objects.equals(this.showReleaseNotes, shopadminProfileSettings.showReleaseNotes) &&
        Objects.equals(this.hideTicketId, shopadminProfileSettings.hideTicketId) &&
        Objects.equals(this.canCancel, shopadminProfileSettings.canCancel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(showTurnover, showTurnoverStaffName, showOpenTickets, showTickets, showCredit, showResults, showReleaseNotes, hideTicketId, canCancel);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShopadminProfileSettings {\n");
    
    sb.append("    showTurnover: ").append(toIndentedString(showTurnover)).append("\n");
    sb.append("    showTurnoverStaffName: ").append(toIndentedString(showTurnoverStaffName)).append("\n");
    sb.append("    showOpenTickets: ").append(toIndentedString(showOpenTickets)).append("\n");
    sb.append("    showTickets: ").append(toIndentedString(showTickets)).append("\n");
    sb.append("    showCredit: ").append(toIndentedString(showCredit)).append("\n");
    sb.append("    showResults: ").append(toIndentedString(showResults)).append("\n");
    sb.append("    showReleaseNotes: ").append(toIndentedString(showReleaseNotes)).append("\n");
    sb.append("    hideTicketId: ").append(toIndentedString(hideTicketId)).append("\n");
    sb.append("    canCancel: ").append(toIndentedString(canCancel)).append("\n");
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
