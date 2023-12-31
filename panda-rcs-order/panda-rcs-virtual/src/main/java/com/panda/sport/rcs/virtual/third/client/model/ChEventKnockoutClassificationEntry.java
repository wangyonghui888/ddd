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
import java.util.ArrayList;
import java.util.List;

/**
 * ChEventKnockoutClassificationEntry
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class ChEventKnockoutClassificationEntry {
  @SerializedName("eventId")
  private Long eventId = null;

  @SerializedName("participants")
  private List<String> participants = null;

  @SerializedName("finalOutcome")
  private List<String> finalOutcome = null;

  @SerializedName("penalty")
  private Boolean penalty = null;

  @SerializedName("classifiedTeam")
  private String classifiedTeam = null;

  public ChEventKnockoutClassificationEntry eventId(Long eventId) {
    this.eventId = eventId;
    return this;
  }

   /**
   * Event ID
   * @return eventId
  **/
  @ApiModelProperty(value = "Event ID")
  public Long getEventId() {
    return eventId;
  }

  public void setEventId(Long eventId) {
    this.eventId = eventId;
  }

  public ChEventKnockoutClassificationEntry participants(List<String> participants) {
    this.participants = participants;
    return this;
  }

  public ChEventKnockoutClassificationEntry addParticipantsItem(String participantsItem) {
    if (this.participants == null) {
      this.participants = new ArrayList<String>();
    }
    this.participants.add(participantsItem);
    return this;
  }

   /**
   * List of competition Participants
   * @return participants
  **/
  @ApiModelProperty(value = "List of competition Participants")
  public List<String> getParticipants() {
    return participants;
  }

  public void setParticipants(List<String> participants) {
    this.participants = participants;
  }

  public ChEventKnockoutClassificationEntry finalOutcome(List<String> finalOutcome) {
    this.finalOutcome = finalOutcome;
    return this;
  }

  public ChEventKnockoutClassificationEntry addFinalOutcomeItem(String finalOutcomeItem) {
    if (this.finalOutcome == null) {
      this.finalOutcome = new ArrayList<String>();
    }
    this.finalOutcome.add(finalOutcomeItem);
    return this;
  }

   /**
   * Final Result Information
   * @return finalOutcome
  **/
  @ApiModelProperty(value = "Final Result Information")
  public List<String> getFinalOutcome() {
    return finalOutcome;
  }

  public void setFinalOutcome(List<String> finalOutcome) {
    this.finalOutcome = finalOutcome;
  }

  public ChEventKnockoutClassificationEntry penalty(Boolean penalty) {
    this.penalty = penalty;
    return this;
  }

   /**
   * There is penalty or not.
   * @return penalty
  **/
  @ApiModelProperty(value = "There is penalty or not.")
  public Boolean isPenalty() {
    return penalty;
  }

  public void setPenalty(Boolean penalty) {
    this.penalty = penalty;
  }

  public ChEventKnockoutClassificationEntry classifiedTeam(String classifiedTeam) {
    this.classifiedTeam = classifiedTeam;
    return this;
  }

   /**
   * ID of classified Team
   * @return classifiedTeam
  **/
  @ApiModelProperty(value = "ID of classified Team")
  public String getClassifiedTeam() {
    return classifiedTeam;
  }

  public void setClassifiedTeam(String classifiedTeam) {
    this.classifiedTeam = classifiedTeam;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChEventKnockoutClassificationEntry chEventKnockoutClassificationEntry = (ChEventKnockoutClassificationEntry) o;
    return Objects.equals(this.eventId, chEventKnockoutClassificationEntry.eventId) &&
        Objects.equals(this.participants, chEventKnockoutClassificationEntry.participants) &&
        Objects.equals(this.finalOutcome, chEventKnockoutClassificationEntry.finalOutcome) &&
        Objects.equals(this.penalty, chEventKnockoutClassificationEntry.penalty) &&
        Objects.equals(this.classifiedTeam, chEventKnockoutClassificationEntry.classifiedTeam);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventId, participants, finalOutcome, penalty, classifiedTeam);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChEventKnockoutClassificationEntry {\n");
    
    sb.append("    eventId: ").append(toIndentedString(eventId)).append("\n");
    sb.append("    participants: ").append(toIndentedString(participants)).append("\n");
    sb.append("    finalOutcome: ").append(toIndentedString(finalOutcome)).append("\n");
    sb.append("    penalty: ").append(toIndentedString(penalty)).append("\n");
    sb.append("    classifiedTeam: ").append(toIndentedString(classifiedTeam)).append("\n");
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

