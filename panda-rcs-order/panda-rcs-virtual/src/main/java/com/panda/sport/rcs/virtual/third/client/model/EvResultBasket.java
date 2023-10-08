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
import com.panda.sport.rcs.virtual.third.client.model.EvResult;
import java.io.IOException;

/**
 * Internal Event Result for Baskets competition 
 */
@ApiModel(description = "Internal Event Result for Baskets competition ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EvResultBasket extends EvResult {
  @SerializedName("resultPointsPregameRed")
  private Integer resultPointsPregameRed = null;

  @SerializedName("resultPointsPregameBlue")
  private Integer resultPointsPregameBlue = null;

  @SerializedName("resultPointsEndMatchRed")
  private Integer resultPointsEndMatchRed = null;

  @SerializedName("resultPointsEndMatchBlue")
  private Integer resultPointsEndMatchBlue = null;

  @SerializedName("fieldGoalTotalRed")
  private Integer fieldGoalTotalRed = null;

  @SerializedName("fieldGoalDoneRed")
  private Integer fieldGoalDoneRed = null;

  @SerializedName("fieldGoalTotalBlue")
  private Integer fieldGoalTotalBlue = null;

  @SerializedName("fieldGoalDoneBlue")
  private Integer fieldGoalDoneBlue = null;

  @SerializedName("twoPointsTotalRed")
  private Integer twoPointsTotalRed = null;

  @SerializedName("twoPointsTotalBlue")
  private Integer twoPointsTotalBlue = null;

  @SerializedName("twoPointsDoneRed")
  private Integer twoPointsDoneRed = null;

  @SerializedName("twoPointsDoneBlue")
  private Integer twoPointsDoneBlue = null;

  @SerializedName("resultDuration")
  private Float resultDuration = null;

  @SerializedName("mediaId")
  private String mediaId = null;

  public EvResultBasket resultPointsPregameRed(Integer resultPointsPregameRed) {
    this.resultPointsPregameRed = resultPointsPregameRed;
    return this;
  }

   /**
   * points of red team at pregame phase end.
   * @return resultPointsPregameRed
  **/
  @ApiModelProperty(value = "points of red team at pregame phase end.")
  public Integer getResultPointsPregameRed() {
    return resultPointsPregameRed;
  }

  public void setResultPointsPregameRed(Integer resultPointsPregameRed) {
    this.resultPointsPregameRed = resultPointsPregameRed;
  }

  public EvResultBasket resultPointsPregameBlue(Integer resultPointsPregameBlue) {
    this.resultPointsPregameBlue = resultPointsPregameBlue;
    return this;
  }

   /**
   * points of blue team at pregame phase end.
   * @return resultPointsPregameBlue
  **/
  @ApiModelProperty(value = "points of blue team at pregame phase end.")
  public Integer getResultPointsPregameBlue() {
    return resultPointsPregameBlue;
  }

  public void setResultPointsPregameBlue(Integer resultPointsPregameBlue) {
    this.resultPointsPregameBlue = resultPointsPregameBlue;
  }

  public EvResultBasket resultPointsEndMatchRed(Integer resultPointsEndMatchRed) {
    this.resultPointsEndMatchRed = resultPointsEndMatchRed;
    return this;
  }

   /**
   * points of red team at match end.
   * @return resultPointsEndMatchRed
  **/
  @ApiModelProperty(value = "points of red team at match end.")
  public Integer getResultPointsEndMatchRed() {
    return resultPointsEndMatchRed;
  }

  public void setResultPointsEndMatchRed(Integer resultPointsEndMatchRed) {
    this.resultPointsEndMatchRed = resultPointsEndMatchRed;
  }

  public EvResultBasket resultPointsEndMatchBlue(Integer resultPointsEndMatchBlue) {
    this.resultPointsEndMatchBlue = resultPointsEndMatchBlue;
    return this;
  }

   /**
   * points of blue team at match end.
   * @return resultPointsEndMatchBlue
  **/
  @ApiModelProperty(value = "points of blue team at match end.")
  public Integer getResultPointsEndMatchBlue() {
    return resultPointsEndMatchBlue;
  }

  public void setResultPointsEndMatchBlue(Integer resultPointsEndMatchBlue) {
    this.resultPointsEndMatchBlue = resultPointsEndMatchBlue;
  }

  public EvResultBasket fieldGoalTotalRed(Integer fieldGoalTotalRed) {
    this.fieldGoalTotalRed = fieldGoalTotalRed;
    return this;
  }

   /**
   * field goals total of red team at match end.
   * @return fieldGoalTotalRed
  **/
  @ApiModelProperty(value = "field goals total of red team at match end.")
  public Integer getFieldGoalTotalRed() {
    return fieldGoalTotalRed;
  }

  public void setFieldGoalTotalRed(Integer fieldGoalTotalRed) {
    this.fieldGoalTotalRed = fieldGoalTotalRed;
  }

  public EvResultBasket fieldGoalDoneRed(Integer fieldGoalDoneRed) {
    this.fieldGoalDoneRed = fieldGoalDoneRed;
    return this;
  }

   /**
   * field goals done of red team at match end.
   * @return fieldGoalDoneRed
  **/
  @ApiModelProperty(value = "field goals done of red team at match end.")
  public Integer getFieldGoalDoneRed() {
    return fieldGoalDoneRed;
  }

  public void setFieldGoalDoneRed(Integer fieldGoalDoneRed) {
    this.fieldGoalDoneRed = fieldGoalDoneRed;
  }

  public EvResultBasket fieldGoalTotalBlue(Integer fieldGoalTotalBlue) {
    this.fieldGoalTotalBlue = fieldGoalTotalBlue;
    return this;
  }

   /**
   * field goals total of blue team at match end.
   * @return fieldGoalTotalBlue
  **/
  @ApiModelProperty(value = "field goals total of blue team at match end.")
  public Integer getFieldGoalTotalBlue() {
    return fieldGoalTotalBlue;
  }

  public void setFieldGoalTotalBlue(Integer fieldGoalTotalBlue) {
    this.fieldGoalTotalBlue = fieldGoalTotalBlue;
  }

  public EvResultBasket fieldGoalDoneBlue(Integer fieldGoalDoneBlue) {
    this.fieldGoalDoneBlue = fieldGoalDoneBlue;
    return this;
  }

   /**
   * field goals done of blue team at match end.
   * @return fieldGoalDoneBlue
  **/
  @ApiModelProperty(value = "field goals done of blue team at match end.")
  public Integer getFieldGoalDoneBlue() {
    return fieldGoalDoneBlue;
  }

  public void setFieldGoalDoneBlue(Integer fieldGoalDoneBlue) {
    this.fieldGoalDoneBlue = fieldGoalDoneBlue;
  }

  public EvResultBasket twoPointsTotalRed(Integer twoPointsTotalRed) {
    this.twoPointsTotalRed = twoPointsTotalRed;
    return this;
  }

   /**
   * 2 points total of red team at match end.
   * @return twoPointsTotalRed
  **/
  @ApiModelProperty(value = "2 points total of red team at match end.")
  public Integer getTwoPointsTotalRed() {
    return twoPointsTotalRed;
  }

  public void setTwoPointsTotalRed(Integer twoPointsTotalRed) {
    this.twoPointsTotalRed = twoPointsTotalRed;
  }

  public EvResultBasket twoPointsTotalBlue(Integer twoPointsTotalBlue) {
    this.twoPointsTotalBlue = twoPointsTotalBlue;
    return this;
  }

   /**
   * 2 points total of blue team at match end.
   * @return twoPointsTotalBlue
  **/
  @ApiModelProperty(value = "2 points total of blue team at match end.")
  public Integer getTwoPointsTotalBlue() {
    return twoPointsTotalBlue;
  }

  public void setTwoPointsTotalBlue(Integer twoPointsTotalBlue) {
    this.twoPointsTotalBlue = twoPointsTotalBlue;
  }

  public EvResultBasket twoPointsDoneRed(Integer twoPointsDoneRed) {
    this.twoPointsDoneRed = twoPointsDoneRed;
    return this;
  }

   /**
   * 2 points done of red team at match end.
   * @return twoPointsDoneRed
  **/
  @ApiModelProperty(value = "2 points done of red team at match end.")
  public Integer getTwoPointsDoneRed() {
    return twoPointsDoneRed;
  }

  public void setTwoPointsDoneRed(Integer twoPointsDoneRed) {
    this.twoPointsDoneRed = twoPointsDoneRed;
  }

  public EvResultBasket twoPointsDoneBlue(Integer twoPointsDoneBlue) {
    this.twoPointsDoneBlue = twoPointsDoneBlue;
    return this;
  }

   /**
   * 2 points done of blue team at match end.
   * @return twoPointsDoneBlue
  **/
  @ApiModelProperty(value = "2 points done of blue team at match end.")
  public Integer getTwoPointsDoneBlue() {
    return twoPointsDoneBlue;
  }

  public void setTwoPointsDoneBlue(Integer twoPointsDoneBlue) {
    this.twoPointsDoneBlue = twoPointsDoneBlue;
  }

  public EvResultBasket resultDuration(Float resultDuration) {
    this.resultDuration = resultDuration;
    return this;
  }

   /**
   * Video result duration.
   * @return resultDuration
  **/
  @ApiModelProperty(value = "Video result duration.")
  public Float getResultDuration() {
    return resultDuration;
  }

  public void setResultDuration(Float resultDuration) {
    this.resultDuration = resultDuration;
  }

  public EvResultBasket mediaId(String mediaId) {
    this.mediaId = mediaId;
    return this;
  }

   /**
   * Video result id.
   * @return mediaId
  **/
  @ApiModelProperty(value = "Video result id.")
  public String getMediaId() {
    return mediaId;
  }

  public void setMediaId(String mediaId) {
    this.mediaId = mediaId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EvResultBasket evResultBasket = (EvResultBasket) o;
    return Objects.equals(this.resultPointsPregameRed, evResultBasket.resultPointsPregameRed) &&
        Objects.equals(this.resultPointsPregameBlue, evResultBasket.resultPointsPregameBlue) &&
        Objects.equals(this.resultPointsEndMatchRed, evResultBasket.resultPointsEndMatchRed) &&
        Objects.equals(this.resultPointsEndMatchBlue, evResultBasket.resultPointsEndMatchBlue) &&
        Objects.equals(this.fieldGoalTotalRed, evResultBasket.fieldGoalTotalRed) &&
        Objects.equals(this.fieldGoalDoneRed, evResultBasket.fieldGoalDoneRed) &&
        Objects.equals(this.fieldGoalTotalBlue, evResultBasket.fieldGoalTotalBlue) &&
        Objects.equals(this.fieldGoalDoneBlue, evResultBasket.fieldGoalDoneBlue) &&
        Objects.equals(this.twoPointsTotalRed, evResultBasket.twoPointsTotalRed) &&
        Objects.equals(this.twoPointsTotalBlue, evResultBasket.twoPointsTotalBlue) &&
        Objects.equals(this.twoPointsDoneRed, evResultBasket.twoPointsDoneRed) &&
        Objects.equals(this.twoPointsDoneBlue, evResultBasket.twoPointsDoneBlue) &&
        Objects.equals(this.resultDuration, evResultBasket.resultDuration) &&
        Objects.equals(this.mediaId, evResultBasket.mediaId) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultPointsPregameRed, resultPointsPregameBlue, resultPointsEndMatchRed, resultPointsEndMatchBlue, fieldGoalTotalRed, fieldGoalDoneRed, fieldGoalTotalBlue, fieldGoalDoneBlue, twoPointsTotalRed, twoPointsTotalBlue, twoPointsDoneRed, twoPointsDoneBlue, resultDuration, mediaId, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EvResultBasket {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    resultPointsPregameRed: ").append(toIndentedString(resultPointsPregameRed)).append("\n");
    sb.append("    resultPointsPregameBlue: ").append(toIndentedString(resultPointsPregameBlue)).append("\n");
    sb.append("    resultPointsEndMatchRed: ").append(toIndentedString(resultPointsEndMatchRed)).append("\n");
    sb.append("    resultPointsEndMatchBlue: ").append(toIndentedString(resultPointsEndMatchBlue)).append("\n");
    sb.append("    fieldGoalTotalRed: ").append(toIndentedString(fieldGoalTotalRed)).append("\n");
    sb.append("    fieldGoalDoneRed: ").append(toIndentedString(fieldGoalDoneRed)).append("\n");
    sb.append("    fieldGoalTotalBlue: ").append(toIndentedString(fieldGoalTotalBlue)).append("\n");
    sb.append("    fieldGoalDoneBlue: ").append(toIndentedString(fieldGoalDoneBlue)).append("\n");
    sb.append("    twoPointsTotalRed: ").append(toIndentedString(twoPointsTotalRed)).append("\n");
    sb.append("    twoPointsTotalBlue: ").append(toIndentedString(twoPointsTotalBlue)).append("\n");
    sb.append("    twoPointsDoneRed: ").append(toIndentedString(twoPointsDoneRed)).append("\n");
    sb.append("    twoPointsDoneBlue: ").append(toIndentedString(twoPointsDoneBlue)).append("\n");
    sb.append("    resultDuration: ").append(toIndentedString(resultDuration)).append("\n");
    sb.append("    mediaId: ").append(toIndentedString(mediaId)).append("\n");
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

