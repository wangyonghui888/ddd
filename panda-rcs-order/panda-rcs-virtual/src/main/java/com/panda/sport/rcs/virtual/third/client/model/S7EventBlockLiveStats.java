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
import com.panda.sport.rcs.virtual.third.client.model.EventBlockLiveStats;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Information about the live stats of Live Loto event. 
 */
@ApiModel(description = "Information about the live stats of Live Loto event. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class S7EventBlockLiveStats extends EventBlockLiveStats {
  @SerializedName("result")
  private List<Integer> result = null;

  public S7EventBlockLiveStats result(List<Integer> result) {
    this.result = result;
    return this;
  }

  public S7EventBlockLiveStats addResultItem(Integer resultItem) {
    if (this.result == null) {
      this.result = new ArrayList<Integer>();
    }
    this.result.add(resultItem);
    return this;
  }

   /**
   * Get result
   * @return result
  **/
  @ApiModelProperty(value = "")
  public List<Integer> getResult() {
    return result;
  }

  public void setResult(List<Integer> result) {
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
    S7EventBlockLiveStats s7EventBlockLiveStats = (S7EventBlockLiveStats) o;
    return Objects.equals(this.result, s7EventBlockLiveStats.result) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class S7EventBlockLiveStats {\n");
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

