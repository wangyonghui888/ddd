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

/**
 * Lk Cashier Profile Game Settings 
 */
@ApiModel(description = "Lk Cashier Profile Game Settings ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class LkCashierProfileGameSettings extends CashierProfileGameSettings {
  @SerializedName("multiplier")
  private Boolean multiplier = null;

  @SerializedName("repeatBet")
  private Boolean repeatBet = null;

  public LkCashierProfileGameSettings multiplier(Boolean multiplier) {
    this.multiplier = multiplier;
    return this;
  }

   /**
   * Active multitier bets mode 
   * @return multiplier
  **/
  @ApiModelProperty(value = "Active multitier bets mode ")
  public Boolean isMultiplier() {
    return multiplier;
  }

  public void setMultiplier(Boolean multiplier) {
    this.multiplier = multiplier;
  }

  public LkCashierProfileGameSettings repeatBet(Boolean repeatBet) {
    this.repeatBet = repeatBet;
    return this;
  }

   /**
   * When checks bet ticket and it&#39;s lose, repeate the sames tips for current event 
   * @return repeatBet
  **/
  @ApiModelProperty(value = "When checks bet ticket and it's lose, repeate the sames tips for current event ")
  public Boolean isRepeatBet() {
    return repeatBet;
  }

  public void setRepeatBet(Boolean repeatBet) {
    this.repeatBet = repeatBet;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LkCashierProfileGameSettings lkCashierProfileGameSettings = (LkCashierProfileGameSettings) o;
    return Objects.equals(this.multiplier, lkCashierProfileGameSettings.multiplier) &&
        Objects.equals(this.repeatBet, lkCashierProfileGameSettings.repeatBet) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(multiplier, repeatBet, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LkCashierProfileGameSettings {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    multiplier: ").append(toIndentedString(multiplier)).append("\n");
    sb.append("    repeatBet: ").append(toIndentedString(repeatBet)).append("\n");
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

