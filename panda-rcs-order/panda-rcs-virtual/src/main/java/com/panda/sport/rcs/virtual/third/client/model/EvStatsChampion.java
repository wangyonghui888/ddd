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
import com.panda.sport.rcs.virtual.third.client.model.ChMatchStatus;
import com.panda.sport.rcs.virtual.third.client.model.EvStats;
import com.panda.sport.rcs.virtual.third.client.model.TeamToTeamPerformance;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal Event Stats for Champions competition 
 */
@ApiModel(description = "Internal Event Stats for Champions competition ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EvStatsChampion extends EvStats {
  @SerializedName("headToHead")
  private List<List<String>> headToHead = null;

  @SerializedName("performance")
  private TeamToTeamPerformance performance = null;

  @SerializedName("lastResults")
  private List<List<ChMatchStatus>> lastResults = null;

  public EvStatsChampion headToHead(List<List<String>> headToHead) {
    this.headToHead = headToHead;
    return this;
  }

  public EvStatsChampion addHeadToHeadItem(List<String> headToHeadItem) {
    if (this.headToHead == null) {
      this.headToHead = new ArrayList<List<String>>();
    }
    this.headToHead.add(headToHeadItem);
    return this;
  }

   /**
   * Information about past matches between both teams.
   * @return headToHead
  **/
  @ApiModelProperty(value = "Information about past matches between both teams.")
  public List<List<String>> getHeadToHead() {
    return headToHead;
  }

  public void setHeadToHead(List<List<String>> headToHead) {
    this.headToHead = headToHead;
  }

  public EvStatsChampion performance(TeamToTeamPerformance performance) {
    this.performance = performance;
    return this;
  }

   /**
   * Get performance
   * @return performance
  **/
  @ApiModelProperty(value = "")
  public TeamToTeamPerformance getPerformance() {
    return performance;
  }

  public void setPerformance(TeamToTeamPerformance performance) {
    this.performance = performance;
  }

  public EvStatsChampion lastResults(List<List<ChMatchStatus>> lastResults) {
    this.lastResults = lastResults;
    return this;
  }

  public EvStatsChampion addLastResultsItem(List<ChMatchStatus> lastResultsItem) {
    if (this.lastResults == null) {
      this.lastResults = new ArrayList<List<ChMatchStatus>>();
    }
    this.lastResults.add(lastResultsItem);
    return this;
  }

   /**
   * Last 5 match results of two team.
   * @return lastResults
  **/
  @ApiModelProperty(value = "Last 5 match results of two team.")
  public List<List<ChMatchStatus>> getLastResults() {
    return lastResults;
  }

  public void setLastResults(List<List<ChMatchStatus>> lastResults) {
    this.lastResults = lastResults;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EvStatsChampion evStatsChampion = (EvStatsChampion) o;
    return Objects.equals(this.headToHead, evStatsChampion.headToHead) &&
        Objects.equals(this.performance, evStatsChampion.performance) &&
        Objects.equals(this.lastResults, evStatsChampion.lastResults) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(headToHead, performance, lastResults, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EvStatsChampion {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    headToHead: ").append(toIndentedString(headToHead)).append("\n");
    sb.append("    performance: ").append(toIndentedString(performance)).append("\n");
    sb.append("    lastResults: ").append(toIndentedString(lastResults)).append("\n");
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

