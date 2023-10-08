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
 * The information of the public lotto five stats. 
 */
@ApiModel(description = "The information of the public lotto five stats. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class LottofiveEventBlockStatsData {
  @SerializedName("week")
  private Double week = null;

  @SerializedName("percentage")
  private Double percentage = null;

  @SerializedName("history")
  private Double history = null;

  public LottofiveEventBlockStatsData week(Double week) {
    this.week = week;
    return this;
  }

   /**
   * The greatest prize of the week.
   * @return week
  **/
  @ApiModelProperty(value = "The greatest prize of the week.")
  public Double getWeek() {
    return week;
  }

  public void setWeek(Double week) {
    this.week = week;
  }

  public LottofiveEventBlockStatsData percentage(Double percentage) {
    this.percentage = percentage;
    return this;
  }

   /**
   * The percentage based on the stake.
   * @return percentage
  **/
  @ApiModelProperty(value = "The percentage based on the stake.")
  public Double getPercentage() {
    return percentage;
  }

  public void setPercentage(Double percentage) {
    this.percentage = percentage;
  }

  public LottofiveEventBlockStatsData history(Double history) {
    this.history = history;
    return this;
  }

   /**
   * The ever greatest prize.
   * @return history
  **/
  @ApiModelProperty(value = "The ever greatest prize.")
  public Double getHistory() {
    return history;
  }

  public void setHistory(Double history) {
    this.history = history;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LottofiveEventBlockStatsData lottofiveEventBlockStatsData = (LottofiveEventBlockStatsData) o;
    return Objects.equals(this.week, lottofiveEventBlockStatsData.week) &&
        Objects.equals(this.percentage, lottofiveEventBlockStatsData.percentage) &&
        Objects.equals(this.history, lottofiveEventBlockStatsData.history);
  }

  @Override
  public int hashCode() {
    return Objects.hash(week, percentage, history);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LottofiveEventBlockStatsData {\n");
    
    sb.append("    week: ").append(toIndentedString(week)).append("\n");
    sb.append("    percentage: ").append(toIndentedString(percentage)).append("\n");
    sb.append("    history: ").append(toIndentedString(history)).append("\n");
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

