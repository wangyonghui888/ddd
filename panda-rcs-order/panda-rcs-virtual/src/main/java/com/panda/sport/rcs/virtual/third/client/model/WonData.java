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
 * Information about won tickets. 
 */
@ApiModel(description = "Information about won tickets. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class WonData {
  @SerializedName("wonCount")
  private Integer wonCount = null;

  @SerializedName("wonAmount")
  private Double wonAmount = null;

  @SerializedName("wonBonus")
  private Double wonBonus = null;

  public WonData wonCount(Integer wonCount) {
    this.wonCount = wonCount;
    return this;
  }

   /**
   * Won Combination number. Only for WON and PAID tickets. 
   * @return wonCount
  **/
  @ApiModelProperty(value = "Won Combination number. Only for WON and PAID tickets. ")
  public Integer getWonCount() {
    return wonCount;
  }

  public void setWonCount(Integer wonCount) {
    this.wonCount = wonCount;
  }

  public WonData wonAmount(Double wonAmount) {
    this.wonAmount = wonAmount;
    return this;
  }

   /**
   * Won money amount without bonus. Only for WON and PAID tickets. 
   * @return wonAmount
  **/
  @ApiModelProperty(value = "Won money amount without bonus. Only for WON and PAID tickets. ")
  public Double getWonAmount() {
    return wonAmount;
  }

  public void setWonAmount(Double wonAmount) {
    this.wonAmount = wonAmount;
  }

  public WonData wonBonus(Double wonBonus) {
    this.wonBonus = wonBonus;
    return this;
  }

   /**
   * Won money bonus amunt. Only for WON and PAID tickets. 
   * @return wonBonus
  **/
  @ApiModelProperty(value = "Won money bonus amunt. Only for WON and PAID tickets. ")
  public Double getWonBonus() {
    return wonBonus;
  }

  public void setWonBonus(Double wonBonus) {
    this.wonBonus = wonBonus;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WonData wonData = (WonData) o;
    return Objects.equals(this.wonCount, wonData.wonCount) &&
        Objects.equals(this.wonAmount, wonData.wonAmount) &&
        Objects.equals(this.wonBonus, wonData.wonBonus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(wonCount, wonAmount, wonBonus);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WonData {\n");
    
    sb.append("    wonCount: ").append(toIndentedString(wonCount)).append("\n");
    sb.append("    wonAmount: ").append(toIndentedString(wonAmount)).append("\n");
    sb.append("    wonBonus: ").append(toIndentedString(wonBonus)).append("\n");
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
