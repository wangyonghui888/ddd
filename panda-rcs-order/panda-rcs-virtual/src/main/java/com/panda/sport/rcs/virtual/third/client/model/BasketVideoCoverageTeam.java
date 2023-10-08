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
 * Video coverage details for each team 
 */
@ApiModel(description = "Video coverage details for each team ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class BasketVideoCoverageTeam {
  @SerializedName("points")
  private Integer points = null;

  @SerializedName("extraPoints")
  private Integer extraPoints = null;

  public BasketVideoCoverageTeam points(Integer points) {
    this.points = points;
    return this;
  }

   /**
   * Get points
   * @return points
  **/
  @ApiModelProperty(value = "")
  public Integer getPoints() {
    return points;
  }

  public void setPoints(Integer points) {
    this.points = points;
  }

  public BasketVideoCoverageTeam extraPoints(Integer extraPoints) {
    this.extraPoints = extraPoints;
    return this;
  }

   /**
   * Get extraPoints
   * @return extraPoints
  **/
  @ApiModelProperty(value = "")
  public Integer getExtraPoints() {
    return extraPoints;
  }

  public void setExtraPoints(Integer extraPoints) {
    this.extraPoints = extraPoints;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasketVideoCoverageTeam basketVideoCoverageTeam = (BasketVideoCoverageTeam) o;
    return Objects.equals(this.points, basketVideoCoverageTeam.points) &&
        Objects.equals(this.extraPoints, basketVideoCoverageTeam.extraPoints);
  }

  @Override
  public int hashCode() {
    return Objects.hash(points, extraPoints);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasketVideoCoverageTeam {\n");
    
    sb.append("    points: ").append(toIndentedString(points)).append("\n");
    sb.append("    extraPoints: ").append(toIndentedString(extraPoints)).append("\n");
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
