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
import com.panda.sport.rcs.virtual.third.client.model.EventStats;
import com.panda.sport.rcs.virtual.third.client.model.TeamToTeamStats;
import java.io.IOException;

/**
 * Information about football stats event. 
 */
@ApiModel(description = "Information about football stats event. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class FootballEventStats extends EventStats {
  @SerializedName("teamToTeam")
  private TeamToTeamStats teamToTeam = null;

  public FootballEventStats teamToTeam(TeamToTeamStats teamToTeam) {
    this.teamToTeam = teamToTeam;
    return this;
  }

   /**
   * Get teamToTeam
   * @return teamToTeam
  **/
  @ApiModelProperty(value = "")
  public TeamToTeamStats getTeamToTeam() {
    return teamToTeam;
  }

  public void setTeamToTeam(TeamToTeamStats teamToTeam) {
    this.teamToTeam = teamToTeam;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FootballEventStats footballEventStats = (FootballEventStats) o;
    return Objects.equals(this.teamToTeam, footballEventStats.teamToTeam) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(teamToTeam, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FootballEventStats {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    teamToTeam: ").append(toIndentedString(teamToTeam)).append("\n");
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
