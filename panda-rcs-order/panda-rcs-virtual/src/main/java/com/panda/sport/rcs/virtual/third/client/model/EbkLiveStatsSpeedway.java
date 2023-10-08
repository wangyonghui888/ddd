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
import com.panda.sport.rcs.virtual.third.client.model.EvResultSpeedway;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Information about the live stats of Speedway event. 
 */
@ApiModel(description = "Information about the live stats of Speedway event. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EbkLiveStatsSpeedway extends EbkLiveStats {
  @SerializedName("result")
  private List<EvResultSpeedway> result = null;

  public EbkLiveStatsSpeedway result(List<EvResultSpeedway> result) {
    this.result = result;
    return this;
  }

  public EbkLiveStatsSpeedway addResultItem(EvResultSpeedway resultItem) {
    if (this.result == null) {
      this.result = new ArrayList<EvResultSpeedway>();
    }
    this.result.add(resultItem);
    return this;
  }

   /**
   * List of the results of Speedway events from event block
   * @return result
  **/
  @ApiModelProperty(value = "List of the results of Speedway events from event block")
  public List<EvResultSpeedway> getResult() {
    return result;
  }

  public void setResult(List<EvResultSpeedway> result) {
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
    EbkLiveStatsSpeedway ebkLiveStatsSpeedway = (EbkLiveStatsSpeedway) o;
    return Objects.equals(this.result, ebkLiveStatsSpeedway.result) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EbkLiveStatsSpeedway {\n");
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

