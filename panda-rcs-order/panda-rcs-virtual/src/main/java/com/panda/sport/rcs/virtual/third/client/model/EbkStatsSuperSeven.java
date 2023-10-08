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
import com.panda.sport.rcs.virtual.third.client.model.EbkStats;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Super 7 statistics for eventblock. 
 */
@ApiModel(description = "Super 7 statistics for eventblock. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EbkStatsSuperSeven extends EbkStats {
  @SerializedName("historic")
  private List<List<Integer>> historic = null;

  public EbkStatsSuperSeven historic(List<List<Integer>> historic) {
    this.historic = historic;
    return this;
  }

  public EbkStatsSuperSeven addHistoricItem(List<Integer> historicItem) {
    if (this.historic == null) {
      this.historic = new ArrayList<List<Integer>>();
    }
    this.historic.add(historicItem);
    return this;
  }

   /**
   * n last result to calculate statistics on client side. 
   * @return historic
  **/
  @ApiModelProperty(value = "n last result to calculate statistics on client side. ")
  public List<List<Integer>> getHistoric() {
    return historic;
  }

  public void setHistoric(List<List<Integer>> historic) {
    this.historic = historic;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EbkStatsSuperSeven ebkStatsSuperSeven = (EbkStatsSuperSeven) o;
    return Objects.equals(this.historic, ebkStatsSuperSeven.historic) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(historic, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EbkStatsSuperSeven {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    historic: ").append(toIndentedString(historic)).append("\n");
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

