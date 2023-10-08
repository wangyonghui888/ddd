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
 * Stats for custom racers. 
 */
@ApiModel(description = "Stats for custom racers. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class CustomRacerStats {
  @SerializedName("winPercent")
  private Double winPercent = null;

  @SerializedName("sinceWon")
  private Integer sinceWon = null;

  @SerializedName("showPercent")
  private Double showPercent = null;

  @SerializedName("sinceShow")
  private Integer sinceShow = null;

  @SerializedName("rating")
  private Double rating = null;

  @SerializedName("starts")
  private Double starts = null;

  public CustomRacerStats winPercent(Double winPercent) {
    this.winPercent = winPercent;
    return this;
  }

   /**
   * The percentage of victory.
   * @return winPercent
  **/
  @ApiModelProperty(value = "The percentage of victory.")
  public Double getWinPercent() {
    return winPercent;
  }

  public void setWinPercent(Double winPercent) {
    this.winPercent = winPercent;
  }

  public CustomRacerStats sinceWon(Integer sinceWon) {
    this.sinceWon = sinceWon;
    return this;
  }

   /**
   * Number of races since the participant does not win.
   * @return sinceWon
  **/
  @ApiModelProperty(value = "Number of races since the participant does not win.")
  public Integer getSinceWon() {
    return sinceWon;
  }

  public void setSinceWon(Integer sinceWon) {
    this.sinceWon = sinceWon;
  }

  public CustomRacerStats showPercent(Double showPercent) {
    this.showPercent = showPercent;
    return this;
  }

   /**
   * The percentage of show.
   * @return showPercent
  **/
  @ApiModelProperty(value = "The percentage of show.")
  public Double getShowPercent() {
    return showPercent;
  }

  public void setShowPercent(Double showPercent) {
    this.showPercent = showPercent;
  }

  public CustomRacerStats sinceShow(Integer sinceShow) {
    this.sinceShow = sinceShow;
    return this;
  }

   /**
   * Number of races since the participant does not show.
   * @return sinceShow
  **/
  @ApiModelProperty(value = "Number of races since the participant does not show.")
  public Integer getSinceShow() {
    return sinceShow;
  }

  public void setSinceShow(Integer sinceShow) {
    this.sinceShow = sinceShow;
  }

  public CustomRacerStats rating(Double rating) {
    this.rating = rating;
    return this;
  }

   /**
   * Rating.
   * @return rating
  **/
  @ApiModelProperty(value = "Rating.")
  public Double getRating() {
    return rating;
  }

  public void setRating(Double rating) {
    this.rating = rating;
  }

  public CustomRacerStats starts(Double starts) {
    this.starts = starts;
    return this;
  }

   /**
   * The starred rating.
   * @return starts
  **/
  @ApiModelProperty(value = "The starred rating.")
  public Double getStarts() {
    return starts;
  }

  public void setStarts(Double starts) {
    this.starts = starts;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CustomRacerStats customRacerStats = (CustomRacerStats) o;
    return Objects.equals(this.winPercent, customRacerStats.winPercent) &&
        Objects.equals(this.sinceWon, customRacerStats.sinceWon) &&
        Objects.equals(this.showPercent, customRacerStats.showPercent) &&
        Objects.equals(this.sinceShow, customRacerStats.sinceShow) &&
        Objects.equals(this.rating, customRacerStats.rating) &&
        Objects.equals(this.starts, customRacerStats.starts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(winPercent, sinceWon, showPercent, sinceShow, rating, starts);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CustomRacerStats {\n");
    
    sb.append("    winPercent: ").append(toIndentedString(winPercent)).append("\n");
    sb.append("    sinceWon: ").append(toIndentedString(sinceWon)).append("\n");
    sb.append("    showPercent: ").append(toIndentedString(showPercent)).append("\n");
    sb.append("    sinceShow: ").append(toIndentedString(sinceShow)).append("\n");
    sb.append("    rating: ").append(toIndentedString(rating)).append("\n");
    sb.append("    starts: ").append(toIndentedString(starts)).append("\n");
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

