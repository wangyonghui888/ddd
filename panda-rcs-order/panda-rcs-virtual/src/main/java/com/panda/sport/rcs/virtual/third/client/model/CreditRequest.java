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
 * Request to bulk operation of reserve method.
 */
@ApiModel(description = "Request to bulk operation of reserve method.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class CreditRequest {
  @SerializedName("extId")
  private String extId = null;

  @SerializedName("currencyCode")
  private String currencyCode = null;

  @SerializedName("entityId")
  private Integer entityId = null;

  public CreditRequest extId(String extId) {
    this.extId = extId;
    return this;
  }

   /**
   * 3rd partity entity id, asssigned through api.
   * @return extId
  **/
  @ApiModelProperty(required = true, value = "3rd partity entity id, asssigned through api.")
  public String getExtId() {
    return extId;
  }

  public void setExtId(String extId) {
    this.extId = extId;
  }

  public CreditRequest currencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
    return this;
  }

   /**
   * ISO Currency code
   * @return currencyCode
  **/
  @ApiModelProperty(required = true, value = "ISO Currency code")
  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public CreditRequest entityId(Integer entityId) {
    this.entityId = entityId;
    return this;
  }

   /**
   * Get entityId
   * @return entityId
  **/
  @ApiModelProperty(value = "")
  public Integer getEntityId() {
    return entityId;
  }

  public void setEntityId(Integer entityId) {
    this.entityId = entityId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreditRequest creditRequest = (CreditRequest) o;
    return Objects.equals(this.extId, creditRequest.extId) &&
        Objects.equals(this.currencyCode, creditRequest.currencyCode) &&
        Objects.equals(this.entityId, creditRequest.entityId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(extId, currencyCode, entityId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreditRequest {\n");
    
    sb.append("    extId: ").append(toIndentedString(extId)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    entityId: ").append(toIndentedString(entityId)).append("\n");
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
