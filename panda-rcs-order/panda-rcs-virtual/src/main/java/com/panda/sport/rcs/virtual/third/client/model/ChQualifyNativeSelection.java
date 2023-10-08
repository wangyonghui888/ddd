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
 * Represents the model to store the odd library NativeSelection.
 */
@ApiModel(description = "Represents the model to store the odd library NativeSelection.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class ChQualifyNativeSelection {
  @SerializedName("name")
  private String name = null;

  @SerializedName("probability")
  private Double probability = null;

  @SerializedName("kValue")
  private Double kValue = null;

  public ChQualifyNativeSelection name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(value = "")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ChQualifyNativeSelection probability(Double probability) {
    this.probability = probability;
    return this;
  }

   /**
   * Get probability
   * @return probability
  **/
  @ApiModelProperty(value = "")
  public Double getProbability() {
    return probability;
  }

  public void setProbability(Double probability) {
    this.probability = probability;
  }

  public ChQualifyNativeSelection kValue(Double kValue) {
    this.kValue = kValue;
    return this;
  }

   /**
   * Get kValue
   * @return kValue
  **/
  @ApiModelProperty(value = "")
  public Double getKValue() {
    return kValue;
  }

  public void setKValue(Double kValue) {
    this.kValue = kValue;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChQualifyNativeSelection chQualifyNativeSelection = (ChQualifyNativeSelection) o;
    return Objects.equals(this.name, chQualifyNativeSelection.name) &&
        Objects.equals(this.probability, chQualifyNativeSelection.probability) &&
        Objects.equals(this.kValue, chQualifyNativeSelection.kValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, probability, kValue);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChQualifyNativeSelection {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    probability: ").append(toIndentedString(probability)).append("\n");
    sb.append("    kValue: ").append(toIndentedString(kValue)).append("\n");
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
