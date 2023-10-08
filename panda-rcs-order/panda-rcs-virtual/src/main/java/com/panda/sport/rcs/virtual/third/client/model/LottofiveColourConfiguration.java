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
import com.panda.sport.rcs.virtual.third.client.model.LottofiveColourType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ColourConfiguration 
 */
@ApiModel(description = "ColourConfiguration ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class LottofiveColourConfiguration {
  @SerializedName("colourType")
  private LottofiveColourType colourType = null;

  @SerializedName("balls")
  private List<Integer> balls = null;

  public LottofiveColourConfiguration colourType(LottofiveColourType colourType) {
    this.colourType = colourType;
    return this;
  }

   /**
   * Get colourType
   * @return colourType
  **/
  @ApiModelProperty(required = true, value = "")
  public LottofiveColourType getColourType() {
    return colourType;
  }

  public void setColourType(LottofiveColourType colourType) {
    this.colourType = colourType;
  }

  public LottofiveColourConfiguration balls(List<Integer> balls) {
    this.balls = balls;
    return this;
  }

  public LottofiveColourConfiguration addBallsItem(Integer ballsItem) {
    if (this.balls == null) {
      this.balls = new ArrayList<Integer>();
    }
    this.balls.add(ballsItem);
    return this;
  }

   /**
   * Balls of this colour
   * @return balls
  **/
  @ApiModelProperty(value = "Balls of this colour")
  public List<Integer> getBalls() {
    return balls;
  }

  public void setBalls(List<Integer> balls) {
    this.balls = balls;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LottofiveColourConfiguration lottofiveColourConfiguration = (LottofiveColourConfiguration) o;
    return Objects.equals(this.colourType, lottofiveColourConfiguration.colourType) &&
        Objects.equals(this.balls, lottofiveColourConfiguration.balls);
  }

  @Override
  public int hashCode() {
    return Objects.hash(colourType, balls);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LottofiveColourConfiguration {\n");
    
    sb.append("    colourType: ").append(toIndentedString(colourType)).append("\n");
    sb.append("    balls: ").append(toIndentedString(balls)).append("\n");
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

