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
import com.panda.sport.rcs.virtual.third.client.model.JackpotCurrencySetting;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.threeten.bp.OffsetDateTime;

/**
 * Jackpot Information 
 */
@ApiModel(description = "Jackpot Information ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")




public class Jackpot {
  @SerializedName("classType")
  private String classType = null;

  @SerializedName("jackpotName")
  private String jackpotName = null;

  @SerializedName("jackpotActive")
  private Boolean jackpotActive = null;

  @SerializedName("jackpotParentId")
  private Integer jackpotParentId = null;

  @SerializedName("jackpotPercent")
  private Double jackpotPercent = null;

  @SerializedName("jackpotPercentReserve")
  private Double jackpotPercentReserve = null;

  @SerializedName("jackpotMode")
  private String jackpotMode = null;

  @SerializedName("amountReserve")
  private Double amountReserve = null;

  @SerializedName("lastChange")
  private OffsetDateTime lastChange = null;

  @SerializedName("showPopup")
  private Boolean showPopup = null;

  @SerializedName("limits")
  private List<JackpotCurrencySetting> limits = null;

  public Jackpot() {
    this.classType = this.getClass().getSimpleName();
  }
  public Jackpot classType(String classType) {
    this.classType = classType;
    return this;
  }

   /**
   * Get classType
   * @return classType
  **/
  @ApiModelProperty(required = true, value = "")
  public String getClassType() {
    return classType;
  }

  public void setClassType(String classType) {
    this.classType = classType;
  }

  public Jackpot jackpotName(String jackpotName) {
    this.jackpotName = jackpotName;
    return this;
  }

   /**
   * Name of the Jackpot 
   * @return jackpotName
  **/
  @ApiModelProperty(required = true, value = "Name of the Jackpot ")
  public String getJackpotName() {
    return jackpotName;
  }

  public void setJackpotName(String jackpotName) {
    this.jackpotName = jackpotName;
  }

  public Jackpot jackpotActive(Boolean jackpotActive) {
    this.jackpotActive = jackpotActive;
    return this;
  }

   /**
   * TODO 
   * @return jackpotActive
  **/
  @ApiModelProperty(value = "TODO ")
  public Boolean isJackpotActive() {
    return jackpotActive;
  }

  public void setJackpotActive(Boolean jackpotActive) {
    this.jackpotActive = jackpotActive;
  }

  public Jackpot jackpotParentId(Integer jackpotParentId) {
    this.jackpotParentId = jackpotParentId;
    return this;
  }

   /**
   * Jackpot Parent Id. 
   * @return jackpotParentId
  **/
  @ApiModelProperty(value = "Jackpot Parent Id. ")
  public Integer getJackpotParentId() {
    return jackpotParentId;
  }

  public void setJackpotParentId(Integer jackpotParentId) {
    this.jackpotParentId = jackpotParentId;
  }

  public Jackpot jackpotPercent(Double jackpotPercent) {
    this.jackpotPercent = jackpotPercent;
    return this;
  }

   /**
   * TODO. 
   * minimum: 0
   * maximum: 100
   * @return jackpotPercent
  **/
  @ApiModelProperty(value = "TODO. ")
  public Double getJackpotPercent() {
    return jackpotPercent;
  }

  public void setJackpotPercent(Double jackpotPercent) {
    this.jackpotPercent = jackpotPercent;
  }

  public Jackpot jackpotPercentReserve(Double jackpotPercentReserve) {
    this.jackpotPercentReserve = jackpotPercentReserve;
    return this;
  }

   /**
   * TODO. 
   * minimum: 0
   * maximum: 100
   * @return jackpotPercentReserve
  **/
  @ApiModelProperty(value = "TODO. ")
  public Double getJackpotPercentReserve() {
    return jackpotPercentReserve;
  }

  public void setJackpotPercentReserve(Double jackpotPercentReserve) {
    this.jackpotPercentReserve = jackpotPercentReserve;
  }

  public Jackpot jackpotMode(String jackpotMode) {
    this.jackpotMode = jackpotMode;
    return this;
  }

   /**
   * TODO. 
   * @return jackpotMode
  **/
  @ApiModelProperty(value = "TODO. ")
  public String getJackpotMode() {
    return jackpotMode;
  }

  public void setJackpotMode(String jackpotMode) {
    this.jackpotMode = jackpotMode;
  }

  public Jackpot amountReserve(Double amountReserve) {
    this.amountReserve = amountReserve;
    return this;
  }

   /**
   * TODO. 
   * @return amountReserve
  **/
  @ApiModelProperty(value = "TODO. ")
  public Double getAmountReserve() {
    return amountReserve;
  }

  public void setAmountReserve(Double amountReserve) {
    this.amountReserve = amountReserve;
  }

  public Jackpot lastChange(OffsetDateTime lastChange) {
    this.lastChange = lastChange;
    return this;
  }

   /**
   * TODO. 
   * @return lastChange
  **/
  @ApiModelProperty(value = "TODO. ")
  public OffsetDateTime getLastChange() {
    return lastChange;
  }

  public void setLastChange(OffsetDateTime lastChange) {
    this.lastChange = lastChange;
  }

  public Jackpot showPopup(Boolean showPopup) {
    this.showPopup = showPopup;
    return this;
  }

   /**
   * TODO. 
   * @return showPopup
  **/
  @ApiModelProperty(value = "TODO. ")
  public Boolean isShowPopup() {
    return showPopup;
  }

  public void setShowPopup(Boolean showPopup) {
    this.showPopup = showPopup;
  }

  public Jackpot limits(List<JackpotCurrencySetting> limits) {
    this.limits = limits;
    return this;
  }

  public Jackpot addLimitsItem(JackpotCurrencySetting limitsItem) {
    if (this.limits == null) {
      this.limits = new ArrayList<JackpotCurrencySetting>();
    }
    this.limits.add(limitsItem);
    return this;
  }

   /**
   * Get limits
   * @return limits
  **/
  @ApiModelProperty(value = "")
  public List<JackpotCurrencySetting> getLimits() {
    return limits;
  }

  public void setLimits(List<JackpotCurrencySetting> limits) {
    this.limits = limits;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Jackpot jackpot = (Jackpot) o;
    return Objects.equals(this.classType, jackpot.classType) &&
        Objects.equals(this.jackpotName, jackpot.jackpotName) &&
        Objects.equals(this.jackpotActive, jackpot.jackpotActive) &&
        Objects.equals(this.jackpotParentId, jackpot.jackpotParentId) &&
        Objects.equals(this.jackpotPercent, jackpot.jackpotPercent) &&
        Objects.equals(this.jackpotPercentReserve, jackpot.jackpotPercentReserve) &&
        Objects.equals(this.jackpotMode, jackpot.jackpotMode) &&
        Objects.equals(this.amountReserve, jackpot.amountReserve) &&
        Objects.equals(this.lastChange, jackpot.lastChange) &&
        Objects.equals(this.showPopup, jackpot.showPopup) &&
        Objects.equals(this.limits, jackpot.limits);
  }

  @Override
  public int hashCode() {
    return Objects.hash(classType, jackpotName, jackpotActive, jackpotParentId, jackpotPercent, jackpotPercentReserve, jackpotMode, amountReserve, lastChange, showPopup, limits);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Jackpot {\n");
    
    sb.append("    classType: ").append(toIndentedString(classType)).append("\n");
    sb.append("    jackpotName: ").append(toIndentedString(jackpotName)).append("\n");
    sb.append("    jackpotActive: ").append(toIndentedString(jackpotActive)).append("\n");
    sb.append("    jackpotParentId: ").append(toIndentedString(jackpotParentId)).append("\n");
    sb.append("    jackpotPercent: ").append(toIndentedString(jackpotPercent)).append("\n");
    sb.append("    jackpotPercentReserve: ").append(toIndentedString(jackpotPercentReserve)).append("\n");
    sb.append("    jackpotMode: ").append(toIndentedString(jackpotMode)).append("\n");
    sb.append("    amountReserve: ").append(toIndentedString(amountReserve)).append("\n");
    sb.append("    lastChange: ").append(toIndentedString(lastChange)).append("\n");
    sb.append("    showPopup: ").append(toIndentedString(showPopup)).append("\n");
    sb.append("    limits: ").append(toIndentedString(limits)).append("\n");
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

