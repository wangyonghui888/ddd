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
import com.panda.sport.rcs.virtual.third.client.model.GenericTax;
import java.io.IOException;

/**
 * Specific tax for payout 
 */
@ApiModel(description = "Specific tax for payout ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class PayoutTax extends GenericTax {
  @SerializedName("excludeStake")
  private Boolean excludeStake = null;

  @SerializedName("threshold")
  private Double threshold = null;

  /**
   * PROGRESSIVE -&gt; Taxes are applied to the remnant of the threshold (winning - threshold) ABSOLUTE    -&gt; Taxes are applied to the total winning 
   */
  @JsonAdapter(CalcModeEnum.Adapter.class)
  public enum CalcModeEnum {
    PROGRESSIVE("PROGRESSIVE"),
    
    ABSOLUTE("ABSOLUTE");

    private String value;

    CalcModeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static CalcModeEnum fromValue(String text) {
      for (CalcModeEnum b : CalcModeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<CalcModeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final CalcModeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public CalcModeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return CalcModeEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("calcMode")
  private CalcModeEnum calcMode = null;

  @SerializedName("includeJackpot")
  private Boolean includeJackpot = null;

  public PayoutTax excludeStake(Boolean excludeStake) {
    this.excludeStake = excludeStake;
    return this;
  }

   /**
   * Exclude stake from payout amount (before taxes apply) 
   * @return excludeStake
  **/
  @ApiModelProperty(value = "Exclude stake from payout amount (before taxes apply) ")
  public Boolean isExcludeStake() {
    return excludeStake;
  }

  public void setExcludeStake(Boolean excludeStake) {
    this.excludeStake = excludeStake;
  }

  public PayoutTax threshold(Double threshold) {
    this.threshold = threshold;
    return this;
  }

   /**
   * Payout amount from which start to apply taxes 
   * minimum: 0
   * @return threshold
  **/
  @ApiModelProperty(value = "Payout amount from which start to apply taxes ")
  public Double getThreshold() {
    return threshold;
  }

  public void setThreshold(Double threshold) {
    this.threshold = threshold;
  }

  public PayoutTax calcMode(CalcModeEnum calcMode) {
    this.calcMode = calcMode;
    return this;
  }

   /**
   * PROGRESSIVE -&gt; Taxes are applied to the remnant of the threshold (winning - threshold) ABSOLUTE    -&gt; Taxes are applied to the total winning 
   * @return calcMode
  **/
  @ApiModelProperty(value = "PROGRESSIVE -> Taxes are applied to the remnant of the threshold (winning - threshold) ABSOLUTE    -> Taxes are applied to the total winning ")
  public CalcModeEnum getCalcMode() {
    return calcMode;
  }

  public void setCalcMode(CalcModeEnum calcMode) {
    this.calcMode = calcMode;
  }

  public PayoutTax includeJackpot(Boolean includeJackpot) {
    this.includeJackpot = includeJackpot;
    return this;
  }

   /**
   * Include jacktop in payout tax calculation 
   * @return includeJackpot
  **/
  @ApiModelProperty(value = "Include jacktop in payout tax calculation ")
  public Boolean isIncludeJackpot() {
    return includeJackpot;
  }

  public void setIncludeJackpot(Boolean includeJackpot) {
    this.includeJackpot = includeJackpot;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PayoutTax payoutTax = (PayoutTax) o;
    return Objects.equals(this.excludeStake, payoutTax.excludeStake) &&
        Objects.equals(this.threshold, payoutTax.threshold) &&
        Objects.equals(this.calcMode, payoutTax.calcMode) &&
        Objects.equals(this.includeJackpot, payoutTax.includeJackpot) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(excludeStake, threshold, calcMode, includeJackpot, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PayoutTax {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    excludeStake: ").append(toIndentedString(excludeStake)).append("\n");
    sb.append("    threshold: ").append(toIndentedString(threshold)).append("\n");
    sb.append("    calcMode: ").append(toIndentedString(calcMode)).append("\n");
    sb.append("    includeJackpot: ").append(toIndentedString(includeJackpot)).append("\n");
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
