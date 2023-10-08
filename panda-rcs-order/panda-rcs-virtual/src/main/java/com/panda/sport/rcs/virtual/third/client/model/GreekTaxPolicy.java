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
import com.panda.sport.rcs.virtual.third.client.model.TaxPolicy;
import java.io.IOException;

/**
 * Greek tax policy 
 */
@ApiModel(description = "Greek tax policy ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class GreekTaxPolicy extends TaxPolicy {
  @SerializedName("columnValue")
  private Double columnValue = null;

  public GreekTaxPolicy columnValue(Double columnValue) {
    this.columnValue = columnValue;
    return this;
  }

   /**
   * Stake can only be a multiplier of this column value 
   * minimum: 0
   * @return columnValue
  **/
  @ApiModelProperty(value = "Stake can only be a multiplier of this column value ")
  public Double getColumnValue() {
    return columnValue;
  }

  public void setColumnValue(Double columnValue) {
    this.columnValue = columnValue;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GreekTaxPolicy greekTaxPolicy = (GreekTaxPolicy) o;
    return Objects.equals(this.columnValue, greekTaxPolicy.columnValue) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(columnValue, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GreekTaxPolicy {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    columnValue: ").append(toIndentedString(columnValue)).append("\n");
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
