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
import com.panda.sport.rcs.virtual.third.client.model.ReportTarget;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Email Report Target
 */
@ApiModel(description = "Email Report Target")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EmailReportTarget extends ReportTarget {
  @SerializedName("destinationEmailList")
  private List<String> destinationEmailList = new ArrayList<String>();

  public EmailReportTarget destinationEmailList(List<String> destinationEmailList) {
    this.destinationEmailList = destinationEmailList;
    return this;
  }

  public EmailReportTarget addDestinationEmailListItem(String destinationEmailListItem) {
    this.destinationEmailList.add(destinationEmailListItem);
    return this;
  }

   /**
   * Email used to send the report
   * @return destinationEmailList
  **/
  @ApiModelProperty(required = true, value = "Email used to send the report")
  public List<String> getDestinationEmailList() {
    return destinationEmailList;
  }

  public void setDestinationEmailList(List<String> destinationEmailList) {
    this.destinationEmailList = destinationEmailList;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmailReportTarget emailReportTarget = (EmailReportTarget) o;
    return Objects.equals(this.destinationEmailList, emailReportTarget.destinationEmailList) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(destinationEmailList, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmailReportTarget {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    destinationEmailList: ").append(toIndentedString(destinationEmailList)).append("\n");
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
