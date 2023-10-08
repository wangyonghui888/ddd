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
 * Body of the entity message that specificate the type 
 */
@ApiModel(description = "Body of the entity message that specificate the type ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")




public class EntityBodyMessage {
  @SerializedName("classType")
  private String classType = null;

  @SerializedName("cause")
  private String cause = null;

  @SerializedName("data")
  private Object data = null;

  public EntityBodyMessage() {
    this.classType = this.getClass().getSimpleName();
  }
  public EntityBodyMessage classType(String classType) {
    this.classType = classType;
    return this;
  }

   /**
   * Specify the type
   * @return classType
  **/
  @ApiModelProperty(required = true, value = "Specify the type")
  public String getClassType() {
    return classType;
  }

  public void setClassType(String classType) {
    this.classType = classType;
  }

  public EntityBodyMessage cause(String cause) {
    this.cause = cause;
    return this;
  }

   /**
   * What has caused the message
   * @return cause
  **/
  @ApiModelProperty(required = true, value = "What has caused the message")
  public String getCause() {
    return cause;
  }

  public void setCause(String cause) {
    this.cause = cause;
  }

  public EntityBodyMessage data(Object data) {
    this.data = data;
    return this;
  }

   /**
   * JSON with the modified values
   * @return data
  **/
  @ApiModelProperty(value = "JSON with the modified values")
  public Object getData() {
    return data;
  }

  public void setData(Object data) {
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
    EntityBodyMessage entityBodyMessage = (EntityBodyMessage) o;
    return Objects.equals(this.classType, entityBodyMessage.classType) &&
        Objects.equals(this.cause, entityBodyMessage.cause) &&
        Objects.equals(this.data, entityBodyMessage.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(classType, cause, data);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EntityBodyMessage {\n");
    
    sb.append("    classType: ").append(toIndentedString(classType)).append("\n");
    sb.append("    cause: ").append(toIndentedString(cause)).append("\n");
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
