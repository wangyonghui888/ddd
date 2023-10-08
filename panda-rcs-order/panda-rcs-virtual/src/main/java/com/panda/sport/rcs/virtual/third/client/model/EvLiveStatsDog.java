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
import com.panda.sport.rcs.virtual.third.client.model.EvLiveStats;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.threeten.bp.OffsetDateTime;

/**
 * Information about the live stats of Dog event. 
 */
@ApiModel(description = "Information about the live stats of Dog event. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EvLiveStatsDog extends EvLiveStats {
  @SerializedName("result")
  private List<Integer> result = null;

  @SerializedName("resultDate")
  private OffsetDateTime resultDate = null;

  @SerializedName("videoSelected")
  private String videoSelected = null;

  @SerializedName("videoEvents")
  private List<Integer> videoEvents = null;

  @SerializedName("videoDuration")
  private BigDecimal videoDuration = null;

  public EvLiveStatsDog result(List<Integer> result) {
    this.result = result;
    return this;
  }

  public EvLiveStatsDog addResultItem(Integer resultItem) {
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

  public EvLiveStatsDog resultDate(OffsetDateTime resultDate) {
    this.resultDate = resultDate;
    return this;
  }

   /**
   * Date of the event result
   * @return resultDate
  **/
  @ApiModelProperty(value = "Date of the event result")
  public OffsetDateTime getResultDate() {
    return resultDate;
  }

  public void setResultDate(OffsetDateTime resultDate) {
    this.resultDate = resultDate;
  }

  public EvLiveStatsDog videoSelected(String videoSelected) {
    this.videoSelected = videoSelected;
    return this;
  }

   /**
   * Video selected for result
   * @return videoSelected
  **/
  @ApiModelProperty(value = "Video selected for result")
  public String getVideoSelected() {
    return videoSelected;
  }

  public void setVideoSelected(String videoSelected) {
    this.videoSelected = videoSelected;
  }

  public EvLiveStatsDog videoEvents(List<Integer> videoEvents) {
    this.videoEvents = videoEvents;
    return this;
  }

  public EvLiveStatsDog addVideoEventsItem(Integer videoEventsItem) {
    if (this.videoEvents == null) {
      this.videoEvents = new ArrayList<Integer>();
    }
    this.videoEvents.add(videoEventsItem);
    return this;
  }

   /**
   * Overtakings
   * @return videoEvents
  **/
  @ApiModelProperty(value = "Overtakings")
  public List<Integer> getVideoEvents() {
    return videoEvents;
  }

  public void setVideoEvents(List<Integer> videoEvents) {
    this.videoEvents = videoEvents;
  }

  public EvLiveStatsDog videoDuration(BigDecimal videoDuration) {
    this.videoDuration = videoDuration;
    return this;
  }

   /**
   * Video duration
   * @return videoDuration
  **/
  @ApiModelProperty(value = "Video duration")
  public BigDecimal getVideoDuration() {
    return videoDuration;
  }

  public void setVideoDuration(BigDecimal videoDuration) {
    this.videoDuration = videoDuration;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EvLiveStatsDog evLiveStatsDog = (EvLiveStatsDog) o;
    return Objects.equals(this.result, evLiveStatsDog.result) &&
        Objects.equals(this.resultDate, evLiveStatsDog.resultDate) &&
        Objects.equals(this.videoSelected, evLiveStatsDog.videoSelected) &&
        Objects.equals(this.videoEvents, evLiveStatsDog.videoEvents) &&
        Objects.equals(this.videoDuration, evLiveStatsDog.videoDuration) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result, resultDate, videoSelected, videoEvents, videoDuration, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EvLiveStatsDog {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
    sb.append("    resultDate: ").append(toIndentedString(resultDate)).append("\n");
    sb.append("    videoSelected: ").append(toIndentedString(videoSelected)).append("\n");
    sb.append("    videoEvents: ").append(toIndentedString(videoEvents)).append("\n");
    sb.append("    videoDuration: ").append(toIndentedString(videoDuration)).append("\n");
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
