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
 * Object describing details about status of report processes.
 */
@ApiModel(description = "Object describing details about status of report processes.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")




public class ReportProcessStatus {
  @SerializedName("classType")
  private String classType = null;

  @SerializedName("success")
  private Boolean success = false;

  @SerializedName("description")
  private String description = null;

  public ReportProcessStatus() {
    this.classType = this.getClass().getSimpleName();
  }
  public ReportProcessStatus classType(String classType) {
    this.classType = classType;
    return this;
  }

   /**
   * Get classType
   * @return classType
  **/
  @ApiModelProperty(required = true, value = "")
  public String getClassType() {
    return classType;
  }

  public void setClassType(String classType) {
    this.classType = classType;
  }

  public ReportProcessStatus success(Boolean success) {
    this.success = success;
    return this;
  }

   /**
   * Indicates if the process was completed successfully.
   * @return success
  **/
  @ApiModelProperty(value = "Indicates if the process was completed successfully.")
  public Boolean isSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public ReportProcessStatus description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Description about process status
   * @return description
  **/
  @ApiModelProperty(value = "Description about process status")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReportProcessStatus reportProcessStatus = (ReportProcessStatus) o;
    return Objects.equals(this.classType, reportProcessStatus.classType) &&
        Objects.equals(this.success, reportProcessStatus.success) &&
        Objects.equals(this.description, reportProcessStatus.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(classType, success, description);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReportProcessStatus {\n");
    
    sb.append("    classType: ").append(toIndentedString(classType)).append("\n");
    sb.append("    success: ").append(toIndentedString(success)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
