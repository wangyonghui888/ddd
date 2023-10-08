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
import com.panda.sport.rcs.virtual.third.client.model.EvKartEventParticipant;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Information about Kart event. 
 */
@ApiModel(description = "Information about Kart event. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EvDataKart extends EvData {
  @SerializedName("participants")
  private List<EvKartEventParticipant> participants = null;

  @SerializedName("type")
  private String type = null;

  @SerializedName("trackCondition")
  private Float trackCondition = null;

  @SerializedName("numParticipants")
  private Integer numParticipants = null;

  @SerializedName("numPodium")
  private Integer numPodium = null;

  public EvDataKart participants(List<EvKartEventParticipant> participants) {
    this.participants = participants;
    return this;
  }

  public EvDataKart addParticipantsItem(EvKartEventParticipant participantsItem) {
    if (this.participants == null) {
      this.participants = new ArrayList<EvKartEventParticipant>();
    }
    this.participants.add(participantsItem);
    return this;
  }

   /**
   * List of event participants and stats for this event
   * @return participants
  **/
  @ApiModelProperty(value = "List of event participants and stats for this event")
  public List<EvKartEventParticipant> getParticipants() {
    return participants;
  }

  public void setParticipants(List<EvKartEventParticipant> participants) {
    this.participants = participants;
  }

  public EvDataKart type(String type) {
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @ApiModelProperty(value = "")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public EvDataKart trackCondition(Float trackCondition) {
    this.trackCondition = trackCondition;
    return this;
  }

   /**
   * Get trackCondition
   * @return trackCondition
  **/
  @ApiModelProperty(value = "")
  public Float getTrackCondition() {
    return trackCondition;
  }

  public void setTrackCondition(Float trackCondition) {
    this.trackCondition = trackCondition;
  }

  public EvDataKart numParticipants(Integer numParticipants) {
    this.numParticipants = numParticipants;
    return this;
  }

   /**
   * Total number of participants who play the race 
   * @return numParticipants
  **/
  @ApiModelProperty(value = "Total number of participants who play the race ")
  public Integer getNumParticipants() {
    return numParticipants;
  }

  public void setNumParticipants(Integer numParticipants) {
    this.numParticipants = numParticipants;
  }

  public EvDataKart numPodium(Integer numPodium) {
    this.numPodium = numPodium;
    return this;
  }

   /**
   * Indicates the number of participants who will be able to take the podium. numPodium &lt; numParticipants &amp;&amp; numPodium &#x3D;&#x3D;&#x3D; EventResult.finalOutcome.lenght 
   * @return numPodium
  **/
  @ApiModelProperty(value = "Indicates the number of participants who will be able to take the podium. numPodium < numParticipants && numPodium === EventResult.finalOutcome.lenght ")
  public Integer getNumPodium() {
    return numPodium;
  }

  public void setNumPodium(Integer numPodium) {
    this.numPodium = numPodium;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EvDataKart evDataKart = (EvDataKart) o;
    return Objects.equals(this.participants, evDataKart.participants) &&
        Objects.equals(this.type, evDataKart.type) &&
        Objects.equals(this.trackCondition, evDataKart.trackCondition) &&
        Objects.equals(this.numParticipants, evDataKart.numParticipants) &&
        Objects.equals(this.numPodium, evDataKart.numPodium) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(participants, type, trackCondition, numParticipants, numPodium, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EvDataKart {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    participants: ").append(toIndentedString(participants)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    trackCondition: ").append(toIndentedString(trackCondition)).append("\n");
    sb.append("    numParticipants: ").append(toIndentedString(numParticipants)).append("\n");
    sb.append("    numPodium: ").append(toIndentedString(numPodium)).append("\n");
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

