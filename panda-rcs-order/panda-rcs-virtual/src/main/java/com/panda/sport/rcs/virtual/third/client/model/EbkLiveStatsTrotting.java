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
import com.panda.sport.rcs.virtual.third.client.model.EbkLiveStats;
import com.panda.sport.rcs.virtual.third.client.model.EvResultTrotting;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Information about the live stats of Trotting event. 
 */
@ApiModel(description = "Information about the live stats of Trotting event. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EbkLiveStatsTrotting extends EbkLiveStats {
  @SerializedName("result")
  private List<EvResultTrotting> result = null;

  public EbkLiveStatsTrotting result(List<EvResultTrotting> result) {
    this.result = result;
    return this;
  }

  public EbkLiveStatsTrotting addResultItem(EvResultTrotting resultItem) {
    if (this.result == null) {
      this.result = new ArrayList<EvResultTrotting>();
    }
    this.result.add(resultItem);
    return this;
  }

   /**
   * List of the results of trotting events from event block
   * @return result
  **/
  @ApiModelProperty(value = "List of the results of trotting events from event block")
  public List<EvResultTrotting> getResult() {
    return result;
  }

  public void setResult(List<EvResultTrotting> result) {
    this.result = result;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EbkLiveStatsTrotting ebkLiveStatsTrotting = (EbkLiveStatsTrotting) o;
    return Objects.equals(this.result, ebkLiveStatsTrotting.result) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EbkLiveStatsTrotting {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
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

