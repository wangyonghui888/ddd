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
 * Object with data of different delivery methods like main/scp/ftp/http post... 
 */
@ApiModel(description = "Object with data of different delivery methods like main/scp/ftp/http post... ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")




public class ReportTarget {
  @SerializedName("classType")
  private String classType = null;

  public ReportTarget() {
    this.classType = this.getClass().getSimpleName();
  }
  public ReportTarget classType(String classType) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReportTarget reportTarget = (ReportTarget) o;
    return Objects.equals(this.classType, reportTarget.classType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(classType);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReportTarget {\n");
    
    sb.append("    classType: ").append(toIndentedString(classType)).append("\n");
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
