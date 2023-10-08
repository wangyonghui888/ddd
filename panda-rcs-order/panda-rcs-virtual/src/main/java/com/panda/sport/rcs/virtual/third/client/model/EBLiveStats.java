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
import com.panda.sport.rcs.virtual.third.client.model.EbkLiveStats;
import java.io.IOException;
import org.threeten.bp.OffsetDateTime;

/**
 * Information about live stats of event blocks. 
 */
@ApiModel(description = "Information about live stats of event blocks. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EBLiveStats {
  @SerializedName("eblockId")
  private Long eblockId = null;

  @SerializedName("playlistId")
  private Integer playlistId = null;

  @SerializedName("extId")
  private String extId = null;

  @SerializedName("liveStats")
  private EbkLiveStats liveStats = null;

  @SerializedName("expireTime")
  private OffsetDateTime expireTime = null;

  public EBLiveStats eblockId(Long eblockId) {
    this.eblockId = eblockId;
    return this;
  }

   /**
   * Get eblockId
   * @return eblockId
  **/
  @ApiModelProperty(required = true, value = "")
  public Long getEblockId() {
    return eblockId;
  }

  public void setEblockId(Long eblockId) {
    this.eblockId = eblockId;
  }

  public EBLiveStats playlistId(Integer playlistId) {
    this.playlistId = playlistId;
    return this;
  }

   /**
   * Get playlistId
   * @return playlistId
  **/
  @ApiModelProperty(required = true, value = "")
  public Integer getPlaylistId() {
    return playlistId;
  }

  public void setPlaylistId(Integer playlistId) {
    this.playlistId = playlistId;
  }

  public EBLiveStats extId(String extId) {
    this.extId = extId;
    return this;
  }

   /**
   * Get extId
   * @return extId
  **/
  @ApiModelProperty(value = "")
  public String getExtId() {
    return extId;
  }

  public void setExtId(String extId) {
    this.extId = extId;
  }

  public EBLiveStats liveStats(EbkLiveStats liveStats) {
    this.liveStats = liveStats;
    return this;
  }

   /**
   * Get liveStats
   * @return liveStats
  **/
  @ApiModelProperty(value = "")
  public EbkLiveStats getLiveStats() {
    return liveStats;
  }

  public void setLiveStats(EbkLiveStats liveStats) {
    this.liveStats = liveStats;
  }

  public EBLiveStats expireTime(OffsetDateTime expireTime) {
    this.expireTime = expireTime;
    return this;
  }

   /**
   * Expire time for current time stats. If empty, LiveStats, are final. 
   * @return expireTime
  **/
  @ApiModelProperty(value = "Expire time for current time stats. If empty, LiveStats, are final. ")
  public OffsetDateTime getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(OffsetDateTime expireTime) {
    this.expireTime = expireTime;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EBLiveStats ebLiveStats = (EBLiveStats) o;
    return Objects.equals(this.eblockId, ebLiveStats.eblockId) &&
        Objects.equals(this.playlistId, ebLiveStats.playlistId) &&
        Objects.equals(this.extId, ebLiveStats.extId) &&
        Objects.equals(this.liveStats, ebLiveStats.liveStats) &&
        Objects.equals(this.expireTime, ebLiveStats.expireTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eblockId, playlistId, extId, liveStats, expireTime);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EBLiveStats {\n");
    
    sb.append("    eblockId: ").append(toIndentedString(eblockId)).append("\n");
    sb.append("    playlistId: ").append(toIndentedString(playlistId)).append("\n");
    sb.append("    extId: ").append(toIndentedString(extId)).append("\n");
    sb.append("    liveStats: ").append(toIndentedString(liveStats)).append("\n");
    sb.append("    expireTime: ").append(toIndentedString(expireTime)).append("\n");
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
