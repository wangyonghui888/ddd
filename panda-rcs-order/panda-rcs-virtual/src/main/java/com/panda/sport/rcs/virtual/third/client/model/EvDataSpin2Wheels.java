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
 * Event data for Spin2Wheels games
 */
@ApiModel(description = "Event data for Spin2Wheels games")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EvDataSpin2Wheels extends EvData {
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

  public EvDataSpin2Wheels croupierName(String croupierName) {
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

  public EvDataSpin2Wheels videoUrl(String videoUrl) {
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

  public EvDataSpin2Wheels presenterVideoUrl(String presenterVideoUrl) {
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

  public EvDataSpin2Wheels presenterImageUrl(String presenterImageUrl) {
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

  public EvDataSpin2Wheels placeBetsIdentUrl(String placeBetsIdentUrl) {
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

  public EvDataSpin2Wheels betsClosedIdentsUrl(String betsClosedIdentsUrl) {
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

  public EvDataSpin2Wheels gameIdentUrl(String gameIdentUrl) {
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

  public EvDataSpin2Wheels drawDate(OffsetDateTime drawDate) {
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

  public EvDataSpin2Wheels updateDate(OffsetDateTime updateDate) {
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
    EvDataSpin2Wheels evDataSpin2Wheels = (EvDataSpin2Wheels) o;
    return Objects.equals(this.croupierName, evDataSpin2Wheels.croupierName) &&
        Objects.equals(this.videoUrl, evDataSpin2Wheels.videoUrl) &&
        Objects.equals(this.presenterVideoUrl, evDataSpin2Wheels.presenterVideoUrl) &&
        Objects.equals(this.presenterImageUrl, evDataSpin2Wheels.presenterImageUrl) &&
        Objects.equals(this.placeBetsIdentUrl, evDataSpin2Wheels.placeBetsIdentUrl) &&
        Objects.equals(this.betsClosedIdentsUrl, evDataSpin2Wheels.betsClosedIdentsUrl) &&
        Objects.equals(this.gameIdentUrl, evDataSpin2Wheels.gameIdentUrl) &&
        Objects.equals(this.drawDate, evDataSpin2Wheels.drawDate) &&
        Objects.equals(this.updateDate, evDataSpin2Wheels.updateDate) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(croupierName, videoUrl, presenterVideoUrl, presenterImageUrl, placeBetsIdentUrl, betsClosedIdentsUrl, gameIdentUrl, drawDate, updateDate, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EvDataSpin2Wheels {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
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

