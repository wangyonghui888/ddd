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
 * This information is only availabe when ticket is resolved and status of ticket es WON. Return null when not available. 
 */
@ApiModel(description = "This information is only availabe when ticket is resolved and status of ticket es WON. Return null when not available. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class ServerTicketWonData {
  @SerializedName("wonAmount")
  private Double wonAmount = null;

  @SerializedName("wonCount")
  private Integer wonCount = null;

  @SerializedName("wonJackpot")
  private Double wonJackpot = null;

  @SerializedName("wonBonus")
  private Double wonBonus = null;

  @SerializedName("wonTaxes")
  private Double wonTaxes = null;

  @SerializedName("wonTaxesPercent")
  private Double wonTaxesPercent = null;

  @SerializedName("totalCredited")
  private Double totalCredited = null;

  public ServerTicketWonData wonAmount(Double wonAmount) {
    this.wonAmount = wonAmount;
    return this;
  }

   /**
   * Amount won with the ticket. 
   * @return wonAmount
  **/
  @ApiModelProperty(value = "Amount won with the ticket. ")
  public Double getWonAmount() {
    return wonAmount;
  }

  public void setWonAmount(Double wonAmount) {
    this.wonAmount = wonAmount;
  }

  public ServerTicketWonData wonCount(Integer wonCount) {
    this.wonCount = wonCount;
    return this;
  }

   /**
   * Won elements number. Only for WON and PAID tickets. 
   * @return wonCount
  **/
  @ApiModelProperty(value = "Won elements number. Only for WON and PAID tickets. ")
  public Integer getWonCount() {
    return wonCount;
  }

  public void setWonCount(Integer wonCount) {
    this.wonCount = wonCount;
  }

  public ServerTicketWonData wonJackpot(Double wonJackpot) {
    this.wonJackpot = wonJackpot;
    return this;
  }

   /**
   * Amount of jackpot won with the ticket. 
   * @return wonJackpot
  **/
  @ApiModelProperty(value = "Amount of jackpot won with the ticket. ")
  public Double getWonJackpot() {
    return wonJackpot;
  }

  public void setWonJackpot(Double wonJackpot) {
    this.wonJackpot = wonJackpot;
  }

  public ServerTicketWonData wonBonus(Double wonBonus) {
    this.wonBonus = wonBonus;
    return this;
  }

   /**
   * Amount of bonus won with the ticket. 
   * @return wonBonus
  **/
  @ApiModelProperty(value = "Amount of bonus won with the ticket. ")
  public Double getWonBonus() {
    return wonBonus;
  }

  public void setWonBonus(Double wonBonus) {
    this.wonBonus = wonBonus;
  }

  public ServerTicketWonData wonTaxes(Double wonTaxes) {
    this.wonTaxes = wonTaxes;
    return this;
  }

   /**
   * Amount of taxes paid with the won of the ticket. 
   * @return wonTaxes
  **/
  @ApiModelProperty(value = "Amount of taxes paid with the won of the ticket. ")
  public Double getWonTaxes() {
    return wonTaxes;
  }

  public void setWonTaxes(Double wonTaxes) {
    this.wonTaxes = wonTaxes;
  }

  public ServerTicketWonData wonTaxesPercent(Double wonTaxesPercent) {
    this.wonTaxesPercent = wonTaxesPercent;
    return this;
  }

   /**
   * Percentage of taxes paid with the won of the ticket. 
   * @return wonTaxesPercent
  **/
  @ApiModelProperty(value = "Percentage of taxes paid with the won of the ticket. ")
  public Double getWonTaxesPercent() {
    return wonTaxesPercent;
  }

  public void setWonTaxesPercent(Double wonTaxesPercent) {
    this.wonTaxesPercent = wonTaxesPercent;
  }

  public ServerTicketWonData totalCredited(Double totalCredited) {
    this.totalCredited = totalCredited;
    return this;
  }

   /**
   * Amount of the credited won amount. This amount must be equal or lower than won amount. 
   * @return totalCredited
  **/
  @ApiModelProperty(value = "Amount of the credited won amount. This amount must be equal or lower than won amount. ")
  public Double getTotalCredited() {
    return totalCredited;
  }

  public void setTotalCredited(Double totalCredited) {
    this.totalCredited = totalCredited;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerTicketWonData serverTicketWonData = (ServerTicketWonData) o;
    return Objects.equals(this.wonAmount, serverTicketWonData.wonAmount) &&
        Objects.equals(this.wonCount, serverTicketWonData.wonCount) &&
        Objects.equals(this.wonJackpot, serverTicketWonData.wonJackpot) &&
        Objects.equals(this.wonBonus, serverTicketWonData.wonBonus) &&
        Objects.equals(this.wonTaxes, serverTicketWonData.wonTaxes) &&
        Objects.equals(this.wonTaxesPercent, serverTicketWonData.wonTaxesPercent) &&
        Objects.equals(this.totalCredited, serverTicketWonData.totalCredited);
  }

  @Override
  public int hashCode() {
    return Objects.hash(wonAmount, wonCount, wonJackpot, wonBonus, wonTaxes, wonTaxesPercent, totalCredited);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServerTicketWonData {\n");
    
    sb.append("    wonAmount: ").append(toIndentedString(wonAmount)).append("\n");
    sb.append("    wonCount: ").append(toIndentedString(wonCount)).append("\n");
    sb.append("    wonJackpot: ").append(toIndentedString(wonJackpot)).append("\n");
    sb.append("    wonBonus: ").append(toIndentedString(wonBonus)).append("\n");
    sb.append("    wonTaxes: ").append(toIndentedString(wonTaxes)).append("\n");
    sb.append("    wonTaxesPercent: ").append(toIndentedString(wonTaxesPercent)).append("\n");
    sb.append("    totalCredited: ").append(toIndentedString(totalCredited)).append("\n");
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

