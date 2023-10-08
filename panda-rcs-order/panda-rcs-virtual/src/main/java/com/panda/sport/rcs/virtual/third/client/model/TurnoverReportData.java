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
import com.panda.sport.rcs.virtual.third.client.model.ReportData;
import com.panda.sport.rcs.virtual.third.client.model.TurnoverReportCurrencyLinesData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.threeten.bp.OffsetDateTime;

/**
 * Internal Turnover report data
 */
@ApiModel(description = "Internal Turnover report data")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class TurnoverReportData extends ReportData {
  @SerializedName("targetEntityId")
  private Integer targetEntityId = null;

  @SerializedName("targetEntityName")
  private String targetEntityName = null;

  @SerializedName("startTime")
  private OffsetDateTime startTime = null;

  @SerializedName("endTime")
  private OffsetDateTime endTime = null;

  @SerializedName("timezone")
  private String timezone = null;

  @SerializedName("data")
  private List<TurnoverReportCurrencyLinesData> data = null;

  public TurnoverReportData targetEntityId(Integer targetEntityId) {
    this.targetEntityId = targetEntityId;
    return this;
  }

   /**
   * Target Entity Id
   * @return targetEntityId
  **/
  @ApiModelProperty(value = "Target Entity Id")
  public Integer getTargetEntityId() {
    return targetEntityId;
  }

  public void setTargetEntityId(Integer targetEntityId) {
    this.targetEntityId = targetEntityId;
  }

  public TurnoverReportData targetEntityName(String targetEntityName) {
    this.targetEntityName = targetEntityName;
    return this;
  }

   /**
   * Target Entity name
   * @return targetEntityName
  **/
  @ApiModelProperty(value = "Target Entity name")
  public String getTargetEntityName() {
    return targetEntityName;
  }

  public void setTargetEntityName(String targetEntityName) {
    this.targetEntityName = targetEntityName;
  }

  public TurnoverReportData startTime(OffsetDateTime startTime) {
    this.startTime = startTime;
    return this;
  }

   /**
   * Turnover start date.
   * @return startTime
  **/
  @ApiModelProperty(value = "Turnover start date.")
  public OffsetDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(OffsetDateTime startTime) {
    this.startTime = startTime;
  }

  public TurnoverReportData endTime(OffsetDateTime endTime) {
    this.endTime = endTime;
    return this;
  }

   /**
   * Turnover end date.
   * @return endTime
  **/
  @ApiModelProperty(value = "Turnover end date.")
  public OffsetDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(OffsetDateTime endTime) {
    this.endTime = endTime;
  }

  public TurnoverReportData timezone(String timezone) {
    this.timezone = timezone;
    return this;
  }

   /**
   * Timezone used
   * @return timezone
  **/
  @ApiModelProperty(value = "Timezone used")
  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public TurnoverReportData data(List<TurnoverReportCurrencyLinesData> data) {
    this.data = data;
    return this;
  }

  public TurnoverReportData addDataItem(TurnoverReportCurrencyLinesData dataItem) {
    if (this.data == null) {
      this.data = new ArrayList<TurnoverReportCurrencyLinesData>();
    }
    this.data.add(dataItem);
    return this;
  }

   /**
   * Array of accounts groupped by currency and playlist.
   * @return data
  **/
  @ApiModelProperty(value = "Array of accounts groupped by currency and playlist.")
  public List<TurnoverReportCurrencyLinesData> getData() {
    return data;
  }

  public void setData(List<TurnoverReportCurrencyLinesData> data) {
    this.data = data;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TurnoverReportData turnoverReportData = (TurnoverReportData) o;
    return Objects.equals(this.targetEntityId, turnoverReportData.targetEntityId) &&
        Objects.equals(this.targetEntityName, turnoverReportData.targetEntityName) &&
        Objects.equals(this.startTime, turnoverReportData.startTime) &&
        Objects.equals(this.endTime, turnoverReportData.endTime) &&
        Objects.equals(this.timezone, turnoverReportData.timezone) &&
        Objects.equals(this.data, turnoverReportData.data) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(targetEntityId, targetEntityName, startTime, endTime, timezone, data, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TurnoverReportData {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    targetEntityId: ").append(toIndentedString(targetEntityId)).append("\n");
    sb.append("    targetEntityName: ").append(toIndentedString(targetEntityName)).append("\n");
    sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
    sb.append("    endTime: ").append(toIndentedString(endTime)).append("\n");
    sb.append("    timezone: ").append(toIndentedString(timezone)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
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

