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
import java.util.ArrayList;
import java.util.List;

/**
 * Object with information about every moved entity 
 */
@ApiModel(description = "Object with information about every moved entity ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class MoveEntityReportRow {
  @SerializedName("originEntityId")
  private Integer originEntityId = null;

  @SerializedName("destinationEntityId")
  private Integer destinationEntityId = null;

  @SerializedName("issues")
  private List<String> issues = null;

  public MoveEntityReportRow originEntityId(Integer originEntityId) {
    this.originEntityId = originEntityId;
    return this;
  }

   /**
   * Origin entity id  
   * @return originEntityId
  **/
  @ApiModelProperty(value = "Origin entity id  ")
  public Integer getOriginEntityId() {
    return originEntityId;
  }

  public void setOriginEntityId(Integer originEntityId) {
    this.originEntityId = originEntityId;
  }

  public MoveEntityReportRow destinationEntityId(Integer destinationEntityId) {
    this.destinationEntityId = destinationEntityId;
    return this;
  }

   /**
   * Entity id of the new entity created in the moving process 
   * @return destinationEntityId
  **/
  @ApiModelProperty(value = "Entity id of the new entity created in the moving process ")
  public Integer getDestinationEntityId() {
    return destinationEntityId;
  }

  public void setDestinationEntityId(Integer destinationEntityId) {
    this.destinationEntityId = destinationEntityId;
  }

  public MoveEntityReportRow issues(List<String> issues) {
    this.issues = issues;
    return this;
  }

  public MoveEntityReportRow addIssuesItem(String issuesItem) {
    if (this.issues == null) {
      this.issues = new ArrayList<String>();
    }
    this.issues.add(issuesItem);
    return this;
  }

   /**
   * List of issues related to the moving process of this entity (credentials, settings, etc.) 
   * @return issues
  **/
  @ApiModelProperty(value = "List of issues related to the moving process of this entity (credentials, settings, etc.) ")
  public List<String> getIssues() {
    return issues;
  }

  public void setIssues(List<String> issues) {
    this.issues = issues;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MoveEntityReportRow moveEntityReportRow = (MoveEntityReportRow) o;
    return Objects.equals(this.originEntityId, moveEntityReportRow.originEntityId) &&
        Objects.equals(this.destinationEntityId, moveEntityReportRow.destinationEntityId) &&
        Objects.equals(this.issues, moveEntityReportRow.issues);
  }

  @Override
  public int hashCode() {
    return Objects.hash(originEntityId, destinationEntityId, issues);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MoveEntityReportRow {\n");
    
    sb.append("    originEntityId: ").append(toIndentedString(originEntityId)).append("\n");
    sb.append("    destinationEntityId: ").append(toIndentedString(destinationEntityId)).append("\n");
    sb.append("    issues: ").append(toIndentedString(issues)).append("\n");
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

