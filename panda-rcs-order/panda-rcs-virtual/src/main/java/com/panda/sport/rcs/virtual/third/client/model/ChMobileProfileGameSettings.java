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
import com.panda.sport.rcs.virtual.third.client.model.MobileProfileGameSettings;
import java.io.IOException;

/**
 * Ch Mobile Profile Game Settings 
 */
@ApiModel(description = "Ch Mobile Profile Game Settings ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class ChMobileProfileGameSettings extends MobileProfileGameSettings {
  /**
   * Type of soccer team text representation to be shown. When 3_LETTERS selected will show the three letters of fifa code. When FULL_TEAM_NAME selected will show the full team name text translation. 
   */
  @JsonAdapter(TeamNameTextTypeEnum.Adapter.class)
  public enum TeamNameTextTypeEnum {
    _3_LETTERS("3_LETTERS"),
    
    FULL_TEAM_NAME("FULL_TEAM_NAME");

    private String value;

    TeamNameTextTypeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static TeamNameTextTypeEnum fromValue(String text) {
      for (TeamNameTextTypeEnum b : TeamNameTextTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<TeamNameTextTypeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final TeamNameTextTypeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public TeamNameTextTypeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return TeamNameTextTypeEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("teamNameTextType")
  private TeamNameTextTypeEnum teamNameTextType = null;

  public ChMobileProfileGameSettings teamNameTextType(TeamNameTextTypeEnum teamNameTextType) {
    this.teamNameTextType = teamNameTextType;
    return this;
  }

   /**
   * Type of soccer team text representation to be shown. When 3_LETTERS selected will show the three letters of fifa code. When FULL_TEAM_NAME selected will show the full team name text translation. 
   * @return teamNameTextType
  **/
  @ApiModelProperty(value = "Type of soccer team text representation to be shown. When 3_LETTERS selected will show the three letters of fifa code. When FULL_TEAM_NAME selected will show the full team name text translation. ")
  public TeamNameTextTypeEnum getTeamNameTextType() {
    return teamNameTextType;
  }

  public void setTeamNameTextType(TeamNameTextTypeEnum teamNameTextType) {
    this.teamNameTextType = teamNameTextType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChMobileProfileGameSettings chMobileProfileGameSettings = (ChMobileProfileGameSettings) o;
    return Objects.equals(this.teamNameTextType, chMobileProfileGameSettings.teamNameTextType) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(teamNameTextType, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChMobileProfileGameSettings {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    teamNameTextType: ").append(toIndentedString(teamNameTextType)).append("\n");
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

