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
import com.panda.sport.rcs.virtual.third.client.model.CommissionReportLineData;
import com.panda.sport.rcs.virtual.third.client.model.ReportData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal Commission Report data
 */
@ApiModel(description = "Internal Commission Report data")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class CommissionReportData extends ReportData {
  @SerializedName("commissionReportLineData")
  private List<CommissionReportLineData> commissionReportLineData = null;

  public CommissionReportData commissionReportLineData(List<CommissionReportLineData> commissionReportLineData) {
    this.commissionReportLineData = commissionReportLineData;
    return this;
  }

  public CommissionReportData addCommissionReportLineDataItem(CommissionReportLineData commissionReportLineDataItem) {
    if (this.commissionReportLineData == null) {
      this.commissionReportLineData = new ArrayList<CommissionReportLineData>();
    }
    this.commissionReportLineData.add(commissionReportLineDataItem);
    return this;
  }

   /**
   * Array of tickets data grouped by date.
   * @return commissionReportLineData
  **/
  @ApiModelProperty(value = "Array of tickets data grouped by date.")
  public List<CommissionReportLineData> getCommissionReportLineData() {
    return commissionReportLineData;
  }

  public void setCommissionReportLineData(List<CommissionReportLineData> commissionReportLineData) {
    this.commissionReportLineData = commissionReportLineData;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommissionReportData commissionReportData = (CommissionReportData) o;
    return Objects.equals(this.commissionReportLineData, commissionReportData.commissionReportLineData) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(commissionReportLineData, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CommissionReportData {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    commissionReportLineData: ").append(toIndentedString(commissionReportLineData)).append("\n");
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

