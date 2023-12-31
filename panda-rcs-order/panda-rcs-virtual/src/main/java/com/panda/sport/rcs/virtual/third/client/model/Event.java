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
import com.panda.sport.rcs.virtual.third.client.model.EventData;
import com.panda.sport.rcs.virtual.third.client.model.EventLiveStats;
import com.panda.sport.rcs.virtual.third.client.model.EventResult;
import com.panda.sport.rcs.virtual.third.client.model.EventStats;
import com.panda.sport.rcs.virtual.third.client.model.EventStatus;
import java.io.IOException;

/**
 * Event entity, that accepts individual bets. 
 */
@ApiModel(description = "Event entity, that accepts individual bets. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class Event {
  @SerializedName("data")
  private EventData data = null;

  @SerializedName("result")
  private EventResult result = null;

  @SerializedName("stats")
  private EventStats stats = null;

  @SerializedName("liveStats")
  private EventLiveStats liveStats = null;

  @SerializedName("serverStatus")
  private EventStatus serverStatus = null;

  @SerializedName("eventId")
  private Long eventId = null;

  @SerializedName("extId")
  private String extId = null;

  @SerializedName("extData")
  private Object extData = null;

  @SerializedName("order")
  private Integer order = null;

  public Event data(EventData data) {
    this.data = data;
    return this;
  }

   /**
   * Get data
   * @return data
  **/
  @ApiModelProperty(value = "")
  public EventData getData() {
    return data;
  }

  public void setData(EventData data) {
    this.data = data;
  }

  public Event result(EventResult result) {
    this.result = result;
    return this;
  }

   /**
   * Get result
   * @return result
  **/
  @ApiModelProperty(value = "")
  public EventResult getResult() {
    return result;
  }

  public void setResult(EventResult result) {
    this.result = result;
  }

  public Event stats(EventStats stats) {
    this.stats = stats;
    return this;
  }

   /**
   * Get stats
   * @return stats
  **/
  @ApiModelProperty(value = "")
  public EventStats getStats() {
    return stats;
  }

  public void setStats(EventStats stats) {
    this.stats = stats;
  }

  public Event liveStats(EventLiveStats liveStats) {
    this.liveStats = liveStats;
    return this;
  }

   /**
   * Get liveStats
   * @return liveStats
  **/
  @ApiModelProperty(value = "")
  public EventLiveStats getLiveStats() {
    return liveStats;
  }

  public void setLiveStats(EventLiveStats liveStats) {
    this.liveStats = liveStats;
  }

  public Event serverStatus(EventStatus serverStatus) {
    this.serverStatus = serverStatus;
    return this;
  }

   /**
   * Get serverStatus
   * @return serverStatus
  **/
  @ApiModelProperty(required = true, value = "")
  public EventStatus getServerStatus() {
    return serverStatus;
  }

  public void setServerStatus(EventStatus serverStatus) {
    this.serverStatus = serverStatus;
  }

  public Event eventId(Long eventId) {
    this.eventId = eventId;
    return this;
  }

   /**
   * Get eventId
   * @return eventId
  **/
  @ApiModelProperty(value = "")
  public Long getEventId() {
    return eventId;
  }

  public void setEventId(Long eventId) {
    this.eventId = eventId;
  }

  public Event extId(String extId) {
    this.extId = extId;
    return this;
  }

   /**
   * Get extId
   * @return extId
  **/
  @ApiModelProperty(value = "")
  public String getExtId() {
    return extId;
  }

  public void setExtId(String extId) {
    this.extId = extId;
  }

  public Event extData(Object extData) {
    this.extData = extData;
    return this;
  }

   /**
   * Data from external systems. Key value elements  
   * @return extData
  **/
  @ApiModelProperty(value = "Data from external systems. Key value elements  ")
  public Object getExtData() {
    return extData;
  }

  public void setExtData(Object extData) {
    this.extData = extData;
  }

  public Event order(Integer order) {
    this.order = order;
    return this;
  }

   /**
   * Order to show in clients. Event mus be order by this atribute 
   * @return order
  **/
  @ApiModelProperty(value = "Order to show in clients. Event mus be order by this atribute ")
  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Event event = (Event) o;
    return Objects.equals(this.data, event.data) &&
        Objects.equals(this.result, event.result) &&
        Objects.equals(this.stats, event.stats) &&
        Objects.equals(this.liveStats, event.liveStats) &&
        Objects.equals(this.serverStatus, event.serverStatus) &&
        Objects.equals(this.eventId, event.eventId) &&
        Objects.equals(this.extId, event.extId) &&
        Objects.equals(this.extData, event.extData) &&
        Objects.equals(this.order, event.order);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, result, stats, liveStats, serverStatus, eventId, extId, extData, order);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Event {\n");
    
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
    sb.append("    stats: ").append(toIndentedString(stats)).append("\n");
    sb.append("    liveStats: ").append(toIndentedString(liveStats)).append("\n");
    sb.append("    serverStatus: ").append(toIndentedString(serverStatus)).append("\n");
    sb.append("    eventId: ").append(toIndentedString(eventId)).append("\n");
    sb.append("    extId: ").append(toIndentedString(extId)).append("\n");
    sb.append("    extData: ").append(toIndentedString(extData)).append("\n");
    sb.append("    order: ").append(toIndentedString(order)).append("\n");
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

