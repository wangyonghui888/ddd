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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Client configurable values for different currency type. 
 */
@ApiModel(description = "Client configurable values for different currency type. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class LocalizationCurrencySettings {
  @SerializedName("key")
  private String key = null;

  @SerializedName("value")
  private List<BigDecimal> value = null;

  @SerializedName("symbol")
  private String symbol = null;

  @SerializedName("decimals")
  private Integer decimals = null;

  @SerializedName("step")
  private Double step = null;

  public LocalizationCurrencySettings key(String key) {
    this.key = key;
    return this;
  }

   /**
   * Currency name. 
   * @return key
  **/
  @ApiModelProperty(value = "Currency name. ")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public LocalizationCurrencySettings value(List<BigDecimal> value) {
    this.value = value;
    return this;
  }

  public LocalizationCurrencySettings addValueItem(BigDecimal valueItem) {
    if (this.value == null) {
      this.value = new ArrayList<BigDecimal>();
    }
    this.value.add(valueItem);
    return this;
  }

   /**
   * Custom currency values for chips. 
   * @return value
  **/
  @ApiModelProperty(value = "Custom currency values for chips. ")
  public List<BigDecimal> getValue() {
    return value;
  }

  public void setValue(List<BigDecimal> value) {
    this.value = value;
  }

  public LocalizationCurrencySettings symbol(String symbol) {
    this.symbol = symbol;
    return this;
  }

   /**
   * Unicode Hexadecimal Symbol for the currency.
   * @return symbol
  **/
  @ApiModelProperty(value = "Unicode Hexadecimal Symbol for the currency.")
  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public LocalizationCurrencySettings decimals(Integer decimals) {
    this.decimals = decimals;
    return this;
  }

   /**
   * Total Number of decimal to display
   * minimum: 0
   * @return decimals
  **/
  @ApiModelProperty(value = "Total Number of decimal to display")
  public Integer getDecimals() {
    return decimals;
  }

  public void setDecimals(Integer decimals) {
    this.decimals = decimals;
  }

  public LocalizationCurrencySettings step(Double step) {
    this.step = step;
    return this;
  }

   /**
   * The currency value must be multiple of this amount. This value should be used in stake, final payout, even in rounding policy. 
   * minimum: 0
   * @return step
  **/
  @ApiModelProperty(value = "The currency value must be multiple of this amount. This value should be used in stake, final payout, even in rounding policy. ")
  public Double getStep() {
    return step;
  }

  public void setStep(Double step) {
    this.step = step;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LocalizationCurrencySettings localizationCurrencySettings = (LocalizationCurrencySettings) o;
    return Objects.equals(this.key, localizationCurrencySettings.key) &&
        Objects.equals(this.value, localizationCurrencySettings.value) &&
        Objects.equals(this.symbol, localizationCurrencySettings.symbol) &&
        Objects.equals(this.decimals, localizationCurrencySettings.decimals) &&
        Objects.equals(this.step, localizationCurrencySettings.step);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value, symbol, decimals, step);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LocalizationCurrencySettings {\n");
    
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    symbol: ").append(toIndentedString(symbol)).append("\n");
    sb.append("    decimals: ").append(toIndentedString(decimals)).append("\n");
    sb.append("    step: ").append(toIndentedString(step)).append("\n");
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

