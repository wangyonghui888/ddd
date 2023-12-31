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

/**
 * Information about stats of event blocks. 
 */
@ApiModel(description = "Information about stats of event blocks. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EBStats {
  @SerializedName("eblockId")
  private Long eblockId = null;

  @SerializedName("playlistId")
  private Integer playlistId = null;

  @SerializedName("extId")
  private String extId = null;

  @SerializedName("stats")
  private EbkStats stats = null;

  public EBStats eblockId(Long eblockId) {
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

  public EBStats playlistId(Integer playlistId) {
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

  public EBStats extId(String extId) {
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

  public EBStats stats(EbkStats stats) {
    this.stats = stats;
    return this;
  }

   /**
   * Get stats
   * @return stats
  **/
  @ApiModelProperty(value = "")
  public EbkStats getStats() {
    return stats;
  }

  public void setStats(EbkStats stats) {
    this.stats = stats;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EBStats ebStats = (EBStats) o;
    return Objects.equals(this.eblockId, ebStats.eblockId) &&
        Objects.equals(this.playlistId, ebStats.playlistId) &&
        Objects.equals(this.extId, ebStats.extId) &&
        Objects.equals(this.stats, ebStats.stats);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eblockId, playlistId, extId, stats);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EBStats {\n");
    
    sb.append("    eblockId: ").append(toIndentedString(eblockId)).append("\n");
    sb.append("    playlistId: ").append(toIndentedString(playlistId)).append("\n");
    sb.append("    extId: ").append(toIndentedString(extId)).append("\n");
    sb.append("    stats: ").append(toIndentedString(stats)).append("\n");
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

