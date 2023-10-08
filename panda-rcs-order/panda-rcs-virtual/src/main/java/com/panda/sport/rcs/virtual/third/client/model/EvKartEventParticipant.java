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
import com.panda.sport.rcs.virtual.third.client.model.EvKartParticipant;
import com.panda.sport.rcs.virtual.third.client.model.RaceStats;
import java.io.IOException;

/**
 * Information about the participant (inmutable info) and race participant stats (mutable info) 
 */
@ApiModel(description = "Information about the participant (inmutable info) and race participant stats (mutable info) ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EvKartEventParticipant {
  @SerializedName("participant")
  private EvKartParticipant participant = null;

  @SerializedName("raceStats")
  private RaceStats raceStats = null;

  public EvKartEventParticipant participant(EvKartParticipant participant) {
    this.participant = participant;
    return this;
  }

   /**
   * Get participant
   * @return participant
  **/
  @ApiModelProperty(value = "")
  public EvKartParticipant getParticipant() {
    return participant;
  }

  public void setParticipant(EvKartParticipant participant) {
    this.participant = participant;
  }

  public EvKartEventParticipant raceStats(RaceStats raceStats) {
    this.raceStats = raceStats;
    return this;
  }

   /**
   * Get raceStats
   * @return raceStats
  **/
  @ApiModelProperty(value = "")
  public RaceStats getRaceStats() {
    return raceStats;
  }

  public void setRaceStats(RaceStats raceStats) {
    this.raceStats = raceStats;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EvKartEventParticipant evKartEventParticipant = (EvKartEventParticipant) o;
    return Objects.equals(this.participant, evKartEventParticipant.participant) &&
        Objects.equals(this.raceStats, evKartEventParticipant.raceStats);
  }

  @Override
  public int hashCode() {
    return Objects.hash(participant, raceStats);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EvKartEventParticipant {\n");
    
    sb.append("    participant: ").append(toIndentedString(participant)).append("\n");
    sb.append("    raceStats: ").append(toIndentedString(raceStats)).append("\n");
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

