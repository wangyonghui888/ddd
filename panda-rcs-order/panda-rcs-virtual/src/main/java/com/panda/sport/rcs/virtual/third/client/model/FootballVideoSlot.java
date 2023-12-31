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
 * TODO 
 */
@ApiModel(description = "TODO ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class FootballVideoSlot {
  @SerializedName("mediaId")
  private String mediaId = null;

  @SerializedName("mediaSignature")
  private String mediaSignature = null;

  @SerializedName("mediaExpireTime")
  private Long mediaExpireTime = null;

  @SerializedName("happenings")
  private String happenings = null;

  public FootballVideoSlot mediaId(String mediaId) {
    this.mediaId = mediaId;
    return this;
  }

   /**
   * Multimedia resource identifier associated with the OCV or HLS to be used in the ingame. This attribute will be used to generate the OCV or HLS video url. For OCV, this identifier is the same for both video and voice-over 
   * @return mediaId
  **/
  @ApiModelProperty(value = "Multimedia resource identifier associated with the OCV or HLS to be used in the ingame. This attribute will be used to generate the OCV or HLS video url. For OCV, this identifier is the same for both video and voice-over ")
  public String getMediaId() {
    return mediaId;
  }

  public void setMediaId(String mediaId) {
    this.mediaId = mediaId;
  }

  public FootballVideoSlot mediaSignature(String mediaSignature) {
    this.mediaSignature = mediaSignature;
    return this;
  }

   /**
   * Server generated time signature. It is used for the securization of multimedia resources. Generation method -&gt; digestfunction (timestamp + InternalApiKey).  This attribute will be used to generate the OCV or HLS video url. 
   * @return mediaSignature
  **/
  @ApiModelProperty(value = "Server generated time signature. It is used for the securization of multimedia resources. Generation method -> digestfunction (timestamp + InternalApiKey).  This attribute will be used to generate the OCV or HLS video url. ")
  public String getMediaSignature() {
    return mediaSignature;
  }

  public void setMediaSignature(String mediaSignature) {
    this.mediaSignature = mediaSignature;
  }

  public FootballVideoSlot mediaExpireTime(Long mediaExpireTime) {
    this.mediaExpireTime = mediaExpireTime;
    return this;
  }

   /**
   * Expiration time for the signature (in UTC). If the current timestamp is older than the one given, the multimedia resources will not be accessible.  This attribute will be used to generate the OCV or HLS video url. 
   * @return mediaExpireTime
  **/
  @ApiModelProperty(value = "Expiration time for the signature (in UTC). If the current timestamp is older than the one given, the multimedia resources will not be accessible.  This attribute will be used to generate the OCV or HLS video url. ")
  public Long getMediaExpireTime() {
    return mediaExpireTime;
  }

  public void setMediaExpireTime(Long mediaExpireTime) {
    this.mediaExpireTime = mediaExpireTime;
  }

  public FootballVideoSlot happenings(String happenings) {
    this.happenings = happenings;
    return this;
  }

   /**
   * Text string encoded with the events that happen in the reproduction of the event, such as when a goal happens. 
   * @return happenings
  **/
  @ApiModelProperty(value = "Text string encoded with the events that happen in the reproduction of the event, such as when a goal happens. ")
  public String getHappenings() {
    return happenings;
  }

  public void setHappenings(String happenings) {
    this.happenings = happenings;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FootballVideoSlot footballVideoSlot = (FootballVideoSlot) o;
    return Objects.equals(this.mediaId, footballVideoSlot.mediaId) &&
        Objects.equals(this.mediaSignature, footballVideoSlot.mediaSignature) &&
        Objects.equals(this.mediaExpireTime, footballVideoSlot.mediaExpireTime) &&
        Objects.equals(this.happenings, footballVideoSlot.happenings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mediaId, mediaSignature, mediaExpireTime, happenings);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FootballVideoSlot {\n");
    
    sb.append("    mediaId: ").append(toIndentedString(mediaId)).append("\n");
    sb.append("    mediaSignature: ").append(toIndentedString(mediaSignature)).append("\n");
    sb.append("    mediaExpireTime: ").append(toIndentedString(mediaExpireTime)).append("\n");
    sb.append("    happenings: ").append(toIndentedString(happenings)).append("\n");
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

