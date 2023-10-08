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
import com.panda.sport.rcs.virtual.third.client.model.JackpotTypeData;
import com.panda.sport.rcs.virtual.third.client.model.JackpotTypeStatus;
import java.io.IOException;

/**
 * Jackpot Message Data
 */
@ApiModel(description = "Jackpot Message Data")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class JackpotMessageData {
  @SerializedName("ticketId")
  private Long ticketId = null;

  @SerializedName("currency")
  private String currency = null;

  @SerializedName("wonAmount")
  private Double wonAmount = null;

  @SerializedName("jackpotTypeStatus")
  private JackpotTypeStatus jackpotTypeStatus = null;

  @SerializedName("jackpotTypeData")
  private JackpotTypeData jackpotTypeData = null;

  public JackpotMessageData ticketId(Long ticketId) {
    this.ticketId = ticketId;
    return this;
  }

   /**
   * The id of the ticket. If (null) a custom ticket will be created.
   * @return ticketId
  **/
  @ApiModelProperty(value = "The id of the ticket. If (null) a custom ticket will be created.")
  public Long getTicketId() {
    return ticketId;
  }

  public void setTicketId(Long ticketId) {
    this.ticketId = ticketId;
  }

  public JackpotMessageData currency(String currency) {
    this.currency = currency;
    return this;
  }

   /**
   * Currency of balance awarded.
   * @return currency
  **/
  @ApiModelProperty(value = "Currency of balance awarded.")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public JackpotMessageData wonAmount(Double wonAmount) {
    this.wonAmount = wonAmount;
    return this;
  }

   /**
   * Won amount on current jackpot.
   * @return wonAmount
  **/
  @ApiModelProperty(value = "Won amount on current jackpot.")
  public Double getWonAmount() {
    return wonAmount;
  }

  public void setWonAmount(Double wonAmount) {
    this.wonAmount = wonAmount;
  }

  public JackpotMessageData jackpotTypeStatus(JackpotTypeStatus jackpotTypeStatus) {
    this.jackpotTypeStatus = jackpotTypeStatus;
    return this;
  }

   /**
   * Get jackpotTypeStatus
   * @return jackpotTypeStatus
  **/
  @ApiModelProperty(value = "")
  public JackpotTypeStatus getJackpotTypeStatus() {
    return jackpotTypeStatus;
  }

  public void setJackpotTypeStatus(JackpotTypeStatus jackpotTypeStatus) {
    this.jackpotTypeStatus = jackpotTypeStatus;
  }

  public JackpotMessageData jackpotTypeData(JackpotTypeData jackpotTypeData) {
    this.jackpotTypeData = jackpotTypeData;
    return this;
  }

   /**
   * Get jackpotTypeData
   * @return jackpotTypeData
  **/
  @ApiModelProperty(value = "")
  public JackpotTypeData getJackpotTypeData() {
    return jackpotTypeData;
  }

  public void setJackpotTypeData(JackpotTypeData jackpotTypeData) {
    this.jackpotTypeData = jackpotTypeData;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JackpotMessageData jackpotMessageData = (JackpotMessageData) o;
    return Objects.equals(this.ticketId, jackpotMessageData.ticketId) &&
        Objects.equals(this.currency, jackpotMessageData.currency) &&
        Objects.equals(this.wonAmount, jackpotMessageData.wonAmount) &&
        Objects.equals(this.jackpotTypeStatus, jackpotMessageData.jackpotTypeStatus) &&
        Objects.equals(this.jackpotTypeData, jackpotMessageData.jackpotTypeData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ticketId, currency, wonAmount, jackpotTypeStatus, jackpotTypeData);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class JackpotMessageData {\n");
    
    sb.append("    ticketId: ").append(toIndentedString(ticketId)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    wonAmount: ").append(toIndentedString(wonAmount)).append("\n");
    sb.append("    jackpotTypeStatus: ").append(toIndentedString(jackpotTypeStatus)).append("\n");
    sb.append("    jackpotTypeData: ").append(toIndentedString(jackpotTypeData)).append("\n");
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

