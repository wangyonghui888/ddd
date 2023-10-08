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
import org.threeten.bp.OffsetDateTime;

/**
 * Object with the Live Lotto data information.
 */
@ApiModel(description = "Object with the Live Lotto data information.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EvDataLiveLotto extends EvData {
  @SerializedName("minBet")
  private Integer minBet = null;

  @SerializedName("maxBet")
  private Integer maxBet = null;

  @SerializedName("croupierName")
  private String croupierName = null;

  @SerializedName("videoUrl")
  private String videoUrl = null;

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

  @SerializedName("drawDate")
  private OffsetDateTime drawDate = null;

  @SerializedName("updateDate")
  private OffsetDateTime updateDate = null;

  public EvDataLiveLotto minBet(Integer minBet) {
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

  public EvDataLiveLotto maxBet(Integer maxBet) {
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

  public EvDataLiveLotto croupierName(String croupierName) {
    this.croupierName = croupierName;
    return this;
  }

   /**
   * Get croupierName
   * @return croupierName
  **/
  @ApiModelProperty(value = "")
  public String getCroupierName() {
    return croupierName;
  }

  public void setCroupierName(String croupierName) {
    this.croupierName = croupierName;
  }

  public EvDataLiveLotto videoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
    return this;
  }

   /**
   * Get videoUrl
   * @return videoUrl
  **/
  @ApiModelProperty(value = "")
  public String getVideoUrl() {
    return videoUrl;
  }

  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }

  public EvDataLiveLotto presenterVideoUrl(String presenterVideoUrl) {
    this.presenterVideoUrl = presenterVideoUrl;
    return this;
  }

   /**
   * Get presenterVideoUrl
   * @return presenterVideoUrl
  **/
  @ApiModelProperty(value = "")
  public String getPresenterVideoUrl() {
    return presenterVideoUrl;
  }

  public void setPresenterVideoUrl(String presenterVideoUrl) {
    this.presenterVideoUrl = presenterVideoUrl;
  }

  public EvDataLiveLotto presenterImageUrl(String presenterImageUrl) {
    this.presenterImageUrl = presenterImageUrl;
    return this;
  }

   /**
   * Get presenterImageUrl
   * @return presenterImageUrl
  **/
  @ApiModelProperty(value = "")
  public String getPresenterImageUrl() {
    return presenterImageUrl;
  }

  public void setPresenterImageUrl(String presenterImageUrl) {
    this.presenterImageUrl = presenterImageUrl;
  }

  public EvDataLiveLotto placeBetsIdentUrl(String placeBetsIdentUrl) {
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

  public EvDataLiveLotto betsClosedIdentsUrl(String betsClosedIdentsUrl) {
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

  public EvDataLiveLotto gameIdentUrl(String gameIdentUrl) {
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

  public EvDataLiveLotto drawDate(OffsetDateTime drawDate) {
    this.drawDate = drawDate;
    return this;
  }

   /**
   * Get drawDate
   * @return drawDate
  **/
  @ApiModelProperty(value = "")
  public OffsetDateTime getDrawDate() {
    return drawDate;
  }

  public void setDrawDate(OffsetDateTime drawDate) {
    this.drawDate = drawDate;
  }

  public EvDataLiveLotto updateDate(OffsetDateTime updateDate) {
    this.updateDate = updateDate;
    return this;
  }

   /**
   * Get updateDate
   * @return updateDate
  **/
  @ApiModelProperty(value = "")
  public OffsetDateTime getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(OffsetDateTime updateDate) {
    this.updateDate = updateDate;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EvDataLiveLotto evDataLiveLotto = (EvDataLiveLotto) o;
    return Objects.equals(this.minBet, evDataLiveLotto.minBet) &&
        Objects.equals(this.maxBet, evDataLiveLotto.maxBet) &&
        Objects.equals(this.croupierName, evDataLiveLotto.croupierName) &&
        Objects.equals(this.videoUrl, evDataLiveLotto.videoUrl) &&
        Objects.equals(this.presenterVideoUrl, evDataLiveLotto.presenterVideoUrl) &&
        Objects.equals(this.presenterImageUrl, evDataLiveLotto.presenterImageUrl) &&
        Objects.equals(this.placeBetsIdentUrl, evDataLiveLotto.placeBetsIdentUrl) &&
        Objects.equals(this.betsClosedIdentsUrl, evDataLiveLotto.betsClosedIdentsUrl) &&
        Objects.equals(this.gameIdentUrl, evDataLiveLotto.gameIdentUrl) &&
        Objects.equals(this.drawDate, evDataLiveLotto.drawDate) &&
        Objects.equals(this.updateDate, evDataLiveLotto.updateDate) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(minBet, maxBet, croupierName, videoUrl, presenterVideoUrl, presenterImageUrl, placeBetsIdentUrl, betsClosedIdentsUrl, gameIdentUrl, drawDate, updateDate, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EvDataLiveLotto {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    minBet: ").append(toIndentedString(minBet)).append("\n");
    sb.append("    maxBet: ").append(toIndentedString(maxBet)).append("\n");
    sb.append("    croupierName: ").append(toIndentedString(croupierName)).append("\n");
    sb.append("    videoUrl: ").append(toIndentedString(videoUrl)).append("\n");
    sb.append("    presenterVideoUrl: ").append(toIndentedString(presenterVideoUrl)).append("\n");
    sb.append("    presenterImageUrl: ").append(toIndentedString(presenterImageUrl)).append("\n");
    sb.append("    placeBetsIdentUrl: ").append(toIndentedString(placeBetsIdentUrl)).append("\n");
    sb.append("    betsClosedIdentsUrl: ").append(toIndentedString(betsClosedIdentsUrl)).append("\n");
    sb.append("    gameIdentUrl: ").append(toIndentedString(gameIdentUrl)).append("\n");
    sb.append("    drawDate: ").append(toIndentedString(drawDate)).append("\n");
    sb.append("    updateDate: ").append(toIndentedString(updateDate)).append("\n");
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

