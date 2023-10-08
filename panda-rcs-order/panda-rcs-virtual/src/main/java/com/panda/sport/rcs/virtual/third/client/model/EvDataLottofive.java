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
import com.panda.sport.rcs.virtual.third.client.model.EvData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Object with the Lotto Five data information.
 */
@ApiModel(description = "Object with the Lotto Five data information.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EvDataLottofive extends EvData {
  @SerializedName("minBet")
  private Integer minBet = null;

  @SerializedName("maxBet")
  private Integer maxBet = null;

  @SerializedName("croupierName")
  private String croupierName = null;

  @SerializedName("videoUrl")
  private String videoUrl = null;

  @SerializedName("backupVideoUrl")
  private String backupVideoUrl = null;

  @SerializedName("presenterVideoUrl")
  private String presenterVideoUrl = null;

  @SerializedName("presenterImageUrl")
  private String presenterImageUrl = null;

  @SerializedName("placeBetsIdentUrl")
  private String placeBetsIdentUrl = null;

  @SerializedName("betsClosedIdentsUrl")
  private String betsClosedIdentsUrl = null;

  @SerializedName("gameIdentUrl")
  private String gameIdentUrl = null;

  @SerializedName("numDraws")
  private Integer numDraws = null;

  @SerializedName("minDrawValue")
  private Integer minDrawValue = null;

  @SerializedName("maxDrawValue")
  private Integer maxDrawValue = null;

  @SerializedName("minNumSelections")
  private Integer minNumSelections = null;

  @SerializedName("maxNumSelections")
  private Integer maxNumSelections = null;

  @SerializedName("name")
  private String name = null;

  @SerializedName("redBalls")
  private List<Integer> redBalls = null;

  @SerializedName("yellowBalls")
  private List<Integer> yellowBalls = null;

  @SerializedName("greenBalls")
  private List<Integer> greenBalls = null;

  public EvDataLottofive minBet(Integer minBet) {
    this.minBet = minBet;
    return this;
  }

   /**
   * Get minBet
   * @return minBet
  **/
  @ApiModelProperty(value = "")
  public Integer getMinBet() {
    return minBet;
  }

  public void setMinBet(Integer minBet) {
    this.minBet = minBet;
  }

  public EvDataLottofive maxBet(Integer maxBet) {
    this.maxBet = maxBet;
    return this;
  }

   /**
   * Get maxBet
   * @return maxBet
  **/
  @ApiModelProperty(value = "")
  public Integer getMaxBet() {
    return maxBet;
  }

  public void setMaxBet(Integer maxBet) {
    this.maxBet = maxBet;
  }

  public EvDataLottofive croupierName(String croupierName) {
    this.croupierName = croupierName;
    return this;
  }

   /**
   * Presenter name
   * @return croupierName
  **/
  @ApiModelProperty(value = "Presenter name")
  public String getCroupierName() {
    return croupierName;
  }

  public void setCroupierName(String croupierName) {
    this.croupierName = croupierName;
  }

  public EvDataLottofive videoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
    return this;
  }

   /**
   * The event video URL
   * @return videoUrl
  **/
  @ApiModelProperty(value = "The event video URL")
  public String getVideoUrl() {
    return videoUrl;
  }

  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }

  public EvDataLottofive backupVideoUrl(String backupVideoUrl) {
    this.backupVideoUrl = backupVideoUrl;
    return this;
  }

   /**
   * Backup event video URL
   * @return backupVideoUrl
  **/
  @ApiModelProperty(value = "Backup event video URL")
  public String getBackupVideoUrl() {
    return backupVideoUrl;
  }

  public void setBackupVideoUrl(String backupVideoUrl) {
    this.backupVideoUrl = backupVideoUrl;
  }

  public EvDataLottofive presenterVideoUrl(String presenterVideoUrl) {
    this.presenterVideoUrl = presenterVideoUrl;
    return this;
  }

   /**
   * Video URL of the presenter of the event
   * @return presenterVideoUrl
  **/
  @ApiModelProperty(value = "Video URL of the presenter of the event")
  public String getPresenterVideoUrl() {
    return presenterVideoUrl;
  }

  public void setPresenterVideoUrl(String presenterVideoUrl) {
    this.presenterVideoUrl = presenterVideoUrl;
  }

  public EvDataLottofive presenterImageUrl(String presenterImageUrl) {
    this.presenterImageUrl = presenterImageUrl;
    return this;
  }

   /**
   * Image URL of the presenter of the event
   * @return presenterImageUrl
  **/
  @ApiModelProperty(value = "Image URL of the presenter of the event")
  public String getPresenterImageUrl() {
    return presenterImageUrl;
  }

  public void setPresenterImageUrl(String presenterImageUrl) {
    this.presenterImageUrl = presenterImageUrl;
  }

  public EvDataLottofive placeBetsIdentUrl(String placeBetsIdentUrl) {
    this.placeBetsIdentUrl = placeBetsIdentUrl;
    return this;
  }

   /**
   * Get placeBetsIdentUrl
   * @return placeBetsIdentUrl
  **/
  @ApiModelProperty(value = "")
  public String getPlaceBetsIdentUrl() {
    return placeBetsIdentUrl;
  }

  public void setPlaceBetsIdentUrl(String placeBetsIdentUrl) {
    this.placeBetsIdentUrl = placeBetsIdentUrl;
  }

  public EvDataLottofive betsClosedIdentsUrl(String betsClosedIdentsUrl) {
    this.betsClosedIdentsUrl = betsClosedIdentsUrl;
    return this;
  }

   /**
   * Get betsClosedIdentsUrl
   * @return betsClosedIdentsUrl
  **/
  @ApiModelProperty(value = "")
  public String getBetsClosedIdentsUrl() {
    return betsClosedIdentsUrl;
  }

  public void setBetsClosedIdentsUrl(String betsClosedIdentsUrl) {
    this.betsClosedIdentsUrl = betsClosedIdentsUrl;
  }

  public EvDataLottofive gameIdentUrl(String gameIdentUrl) {
    this.gameIdentUrl = gameIdentUrl;
    return this;
  }

   /**
   * Get gameIdentUrl
   * @return gameIdentUrl
  **/
  @ApiModelProperty(value = "")
  public String getGameIdentUrl() {
    return gameIdentUrl;
  }

  public void setGameIdentUrl(String gameIdentUrl) {
    this.gameIdentUrl = gameIdentUrl;
  }

  public EvDataLottofive numDraws(Integer numDraws) {
    this.numDraws = numDraws;
    return this;
  }

   /**
   * Number of draw balls of the event
   * @return numDraws
  **/
  @ApiModelProperty(value = "Number of draw balls of the event")
  public Integer getNumDraws() {
    return numDraws;
  }

  public void setNumDraws(Integer numDraws) {
    this.numDraws = numDraws;
  }

  public EvDataLottofive minDrawValue(Integer minDrawValue) {
    this.minDrawValue = minDrawValue;
    return this;
  }

   /**
   * Minimum value for draw ball
   * @return minDrawValue
  **/
  @ApiModelProperty(value = "Minimum value for draw ball")
  public Integer getMinDrawValue() {
    return minDrawValue;
  }

  public void setMinDrawValue(Integer minDrawValue) {
    this.minDrawValue = minDrawValue;
  }

  public EvDataLottofive maxDrawValue(Integer maxDrawValue) {
    this.maxDrawValue = maxDrawValue;
    return this;
  }

   /**
   * Maximum value for draw ball
   * @return maxDrawValue
  **/
  @ApiModelProperty(value = "Maximum value for draw ball")
  public Integer getMaxDrawValue() {
    return maxDrawValue;
  }

  public void setMaxDrawValue(Integer maxDrawValue) {
    this.maxDrawValue = maxDrawValue;
  }

  public EvDataLottofive minNumSelections(Integer minNumSelections) {
    this.minNumSelections = minNumSelections;
    return this;
  }

   /**
   * Minimum number of selections for a bet
   * @return minNumSelections
  **/
  @ApiModelProperty(value = "Minimum number of selections for a bet")
  public Integer getMinNumSelections() {
    return minNumSelections;
  }

  public void setMinNumSelections(Integer minNumSelections) {
    this.minNumSelections = minNumSelections;
  }

  public EvDataLottofive maxNumSelections(Integer maxNumSelections) {
    this.maxNumSelections = maxNumSelections;
    return this;
  }

   /**
   * Maximum number of selections for a bet
   * @return maxNumSelections
  **/
  @ApiModelProperty(value = "Maximum number of selections for a bet")
  public Integer getMaxNumSelections() {
    return maxNumSelections;
  }

  public void setMaxNumSelections(Integer maxNumSelections) {
    this.maxNumSelections = maxNumSelections;
  }

  public EvDataLottofive name(String name) {
    this.name = name;
    return this;
  }

   /**
   * The name of the event
   * @return name
  **/
  @ApiModelProperty(value = "The name of the event")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public EvDataLottofive redBalls(List<Integer> redBalls) {
    this.redBalls = redBalls;
    return this;
  }

  public EvDataLottofive addRedBallsItem(Integer redBallsItem) {
    if (this.redBalls == null) {
      this.redBalls = new ArrayList<Integer>();
    }
    this.redBalls.add(redBallsItem);
    return this;
  }

   /**
   * Ordered array of numbers with red colour.
   * @return redBalls
  **/
  @ApiModelProperty(value = "Ordered array of numbers with red colour.")
  public List<Integer> getRedBalls() {
    return redBalls;
  }

  public void setRedBalls(List<Integer> redBalls) {
    this.redBalls = redBalls;
  }

  public EvDataLottofive yellowBalls(List<Integer> yellowBalls) {
    this.yellowBalls = yellowBalls;
    return this;
  }

  public EvDataLottofive addYellowBallsItem(Integer yellowBallsItem) {
    if (this.yellowBalls == null) {
      this.yellowBalls = new ArrayList<Integer>();
    }
    this.yellowBalls.add(yellowBallsItem);
    return this;
  }

   /**
   * Ordered array of numbers with yellow colour.
   * @return yellowBalls
  **/
  @ApiModelProperty(value = "Ordered array of numbers with yellow colour.")
  public List<Integer> getYellowBalls() {
    return yellowBalls;
  }

  public void setYellowBalls(List<Integer> yellowBalls) {
    this.yellowBalls = yellowBalls;
  }

  public EvDataLottofive greenBalls(List<Integer> greenBalls) {
    this.greenBalls = greenBalls;
    return this;
  }

  public EvDataLottofive addGreenBallsItem(Integer greenBallsItem) {
    if (this.greenBalls == null) {
      this.greenBalls = new ArrayList<Integer>();
    }
    this.greenBalls.add(greenBallsItem);
    return this;
  }

   /**
   * Ordered array of numbers with green colour.
   * @return greenBalls
  **/
  @ApiModelProperty(value = "Ordered array of numbers with green colour.")
  public List<Integer> getGreenBalls() {
    return greenBalls;
  }

  public void setGreenBalls(List<Integer> greenBalls) {
    this.greenBalls = greenBalls;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EvDataLottofive evDataLottofive = (EvDataLottofive) o;
    return Objects.equals(this.minBet, evDataLottofive.minBet) &&
        Objects.equals(this.maxBet, evDataLottofive.maxBet) &&
        Objects.equals(this.croupierName, evDataLottofive.croupierName) &&
        Objects.equals(this.videoUrl, evDataLottofive.videoUrl) &&
        Objects.equals(this.backupVideoUrl, evDataLottofive.backupVideoUrl) &&
        Objects.equals(this.presenterVideoUrl, evDataLottofive.presenterVideoUrl) &&
        Objects.equals(this.presenterImageUrl, evDataLottofive.presenterImageUrl) &&
        Objects.equals(this.placeBetsIdentUrl, evDataLottofive.placeBetsIdentUrl) &&
        Objects.equals(this.betsClosedIdentsUrl, evDataLottofive.betsClosedIdentsUrl) &&
        Objects.equals(this.gameIdentUrl, evDataLottofive.gameIdentUrl) &&
        Objects.equals(this.numDraws, evDataLottofive.numDraws) &&
        Objects.equals(this.minDrawValue, evDataLottofive.minDrawValue) &&
        Objects.equals(this.maxDrawValue, evDataLottofive.maxDrawValue) &&
        Objects.equals(this.minNumSelections, evDataLottofive.minNumSelections) &&
        Objects.equals(this.maxNumSelections, evDataLottofive.maxNumSelections) &&
        Objects.equals(this.name, evDataLottofive.name) &&
        Objects.equals(this.redBalls, evDataLottofive.redBalls) &&
        Objects.equals(this.yellowBalls, evDataLottofive.yellowBalls) &&
        Objects.equals(this.greenBalls, evDataLottofive.greenBalls) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(minBet, maxBet, croupierName, videoUrl, backupVideoUrl, presenterVideoUrl, presenterImageUrl, placeBetsIdentUrl, betsClosedIdentsUrl, gameIdentUrl, numDraws, minDrawValue, maxDrawValue, minNumSelections, maxNumSelections, name, redBalls, yellowBalls, greenBalls, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EvDataLottofive {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    minBet: ").append(toIndentedString(minBet)).append("\n");
    sb.append("    maxBet: ").append(toIndentedString(maxBet)).append("\n");
    sb.append("    croupierName: ").append(toIndentedString(croupierName)).append("\n");
    sb.append("    videoUrl: ").append(toIndentedString(videoUrl)).append("\n");
    sb.append("    backupVideoUrl: ").append(toIndentedString(backupVideoUrl)).append("\n");
    sb.append("    presenterVideoUrl: ").append(toIndentedString(presenterVideoUrl)).append("\n");
    sb.append("    presenterImageUrl: ").append(toIndentedString(presenterImageUrl)).append("\n");
    sb.append("    placeBetsIdentUrl: ").append(toIndentedString(placeBetsIdentUrl)).append("\n");
    sb.append("    betsClosedIdentsUrl: ").append(toIndentedString(betsClosedIdentsUrl)).append("\n");
    sb.append("    gameIdentUrl: ").append(toIndentedString(gameIdentUrl)).append("\n");
    sb.append("    numDraws: ").append(toIndentedString(numDraws)).append("\n");
    sb.append("    minDrawValue: ").append(toIndentedString(minDrawValue)).append("\n");
    sb.append("    maxDrawValue: ").append(toIndentedString(maxDrawValue)).append("\n");
    sb.append("    minNumSelections: ").append(toIndentedString(minNumSelections)).append("\n");
    sb.append("    maxNumSelections: ").append(toIndentedString(maxNumSelections)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    redBalls: ").append(toIndentedString(redBalls)).append("\n");
    sb.append("    yellowBalls: ").append(toIndentedString(yellowBalls)).append("\n");
    sb.append("    greenBalls: ").append(toIndentedString(greenBalls)).append("\n");
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

