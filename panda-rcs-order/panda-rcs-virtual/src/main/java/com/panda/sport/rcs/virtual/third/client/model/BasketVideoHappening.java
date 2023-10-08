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
 * Class responsible for storing information regarding the happening of basket 
 */
@ApiModel(description = "Class responsible for storing information regarding the happening of basket ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class BasketVideoHappening {
  @SerializedName("time")
  private Float time = null;

  /**
   * Listed indicating the types of events: BEGIN &#x3D; It occurs when the match begins (the referee releases the ball) END &#x3D; It occurs when the referee makes the final whistle, players celebrating and ends the match PLAY &#x3D; It happens when the match timer starts running PAUSE &#x3D; Stop the match timer EXTRA_TIME &#x3D; Mark that indicates that you will see extratime at the end of the minute when there has been a draw. POSSESSION_RED &#x3D; Mark indicating that the Red team has possession. Just at the moment the player of the team in question catches the ball. Only marked at the beginning of the match POSSESSION_BLUE &#x3D; Mark indicating that the Blue team has possession. Just at the moment the player of the team in question catches the ball. Only marked at the beginning of the match   END_POSSESION &#x3D;Marker indicating that the 14 seconds of possession has ended. ANNOTATION &#x3D; Marker indicating that the ball has been hit by any team STALLING &#x3D; Marker indicating a stop for unexpected situations (player falls, etc.), that is, when a foul has not occurred or when there has been no ball court exit OUT_RED &#x3D;  Marker indicating a ball court exit by the Red team OUT_BLUE &#x3D; Marker indicating a ball court exit by the Blue team FREE_THROW_1 &#x3D; A personal foul occurs within the triple area and produces 1 free throw FREE_THROW_2 &#x3D; There is a personal foul outside the triple area and produces 2 free throws WIN_21_RED &#x3D; Indicates when the Red team reaches 21. It is indicated by the referee WIN_21_BLUE &#x3D; Indicates when the Blue team reaches 21. It is indicated by the referee WIN_TIME_RED &#x3D; Indicates that the red team has won. To know if a team has won by points at the end of the minute without reaching 21, we will have to check that before this signal there is no WIN_21_RED WIN_TIME_BLUE &#x3D; Indicates that the blue team has won. To know if a team has won by points at the end of the minute without reaching 21, we will have to check that before this signal there is no WIN_21_BLUE RES_TIME_DRAW &#x3D; Indicates that there has been a draw. An EXTRA_TIME will always be launched WIN_EXTRATIME_RED &#x3D; Win the Red team in extra time. It is thrown immediately when the ball enters the basket and makes it reach 2 points. WIN_EXTRATIME_BLUE &#x3D; Win the Blue team in extra time. It is thrown immediately when the ball enters the basket and makes it reach 2 points. 
   */
  @JsonAdapter(EventEnum.Adapter.class)
  public enum EventEnum {
    BEGIN("BEGIN"),
    
    END("END"),
    
    PLAY("PLAY"),
    
    PAUSE("PAUSE"),
    
    EXTRA_TIME("EXTRA_TIME"),
    
    POSSESSION_RED("POSSESSION_RED"),
    
    POSSESSION_BLUE("POSSESSION_BLUE"),
    
    END_POSSESION("END_POSSESION"),
    
    ANNOTATION("ANNOTATION"),
    
    STALLING("STALLING"),
    
    OUT_RED("OUT_RED"),
    
    OUT_BLUE("OUT_BLUE"),
    
    FREE_THROW_1("FREE_THROW_1"),
    
    FREE_THROW_2("FREE_THROW_2"),
    
    WIN_21_RED("WIN_21_RED"),
    
    WIN_21_BLUE("WIN_21_BLUE"),
    
    WIN_TIME_RED("WIN_TIME_RED"),
    
    WIN_TIME_BLUE("WIN_TIME_BLUE"),
    
    RES_TIME_DRAW("RES_TIME_DRAW"),
    
    WIN_EXTRATIME_RED("WIN_EXTRATIME_RED"),
    
    WIN_EXTRATIME_BLUE("WIN_EXTRATIME_BLUE");

    private String value;

    EventEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static EventEnum fromValue(String text) {
      for (EventEnum b : EventEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<EventEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final EventEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public EventEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return EventEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("event")
  private EventEnum event = null;

  /**
   * For ANNOTATION happenings, the team on which the happening takes place. For the rest, null Listed that identifies the team that performs an action: RED &#x3D; \&quot;Red Team. It is mapped in other parts of the system with TeamA BLUE &#x3D; Blue Team. It is mapped in other parts of the system with TeamB 
   */
  @JsonAdapter(TeamEnum.Adapter.class)
  public enum TeamEnum {
    RED("RED"),
    
    BLUE("BLUE");

    private String value;

    TeamEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static TeamEnum fromValue(String text) {
      for (TeamEnum b : TeamEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<TeamEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final TeamEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public TeamEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return TeamEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("team")
  private TeamEnum team = null;

  @SerializedName("playerId")
  private Integer playerId = null;

  @SerializedName("points")
  private Integer points = null;

  public BasketVideoHappening time(Float time) {
    this.time = time;
    return this;
  }

   /**
   * Instante de tiempo en el que sucede el happening 
   * @return time
  **/
  @ApiModelProperty(value = "Instante de tiempo en el que sucede el happening ")
  public Float getTime() {
    return time;
  }

  public void setTime(Float time) {
    this.time = time;
  }

  public BasketVideoHappening event(EventEnum event) {
    this.event = event;
    return this;
  }

   /**
   * Listed indicating the types of events: BEGIN &#x3D; It occurs when the match begins (the referee releases the ball) END &#x3D; It occurs when the referee makes the final whistle, players celebrating and ends the match PLAY &#x3D; It happens when the match timer starts running PAUSE &#x3D; Stop the match timer EXTRA_TIME &#x3D; Mark that indicates that you will see extratime at the end of the minute when there has been a draw. POSSESSION_RED &#x3D; Mark indicating that the Red team has possession. Just at the moment the player of the team in question catches the ball. Only marked at the beginning of the match POSSESSION_BLUE &#x3D; Mark indicating that the Blue team has possession. Just at the moment the player of the team in question catches the ball. Only marked at the beginning of the match   END_POSSESION &#x3D;Marker indicating that the 14 seconds of possession has ended. ANNOTATION &#x3D; Marker indicating that the ball has been hit by any team STALLING &#x3D; Marker indicating a stop for unexpected situations (player falls, etc.), that is, when a foul has not occurred or when there has been no ball court exit OUT_RED &#x3D;  Marker indicating a ball court exit by the Red team OUT_BLUE &#x3D; Marker indicating a ball court exit by the Blue team FREE_THROW_1 &#x3D; A personal foul occurs within the triple area and produces 1 free throw FREE_THROW_2 &#x3D; There is a personal foul outside the triple area and produces 2 free throws WIN_21_RED &#x3D; Indicates when the Red team reaches 21. It is indicated by the referee WIN_21_BLUE &#x3D; Indicates when the Blue team reaches 21. It is indicated by the referee WIN_TIME_RED &#x3D; Indicates that the red team has won. To know if a team has won by points at the end of the minute without reaching 21, we will have to check that before this signal there is no WIN_21_RED WIN_TIME_BLUE &#x3D; Indicates that the blue team has won. To know if a team has won by points at the end of the minute without reaching 21, we will have to check that before this signal there is no WIN_21_BLUE RES_TIME_DRAW &#x3D; Indicates that there has been a draw. An EXTRA_TIME will always be launched WIN_EXTRATIME_RED &#x3D; Win the Red team in extra time. It is thrown immediately when the ball enters the basket and makes it reach 2 points. WIN_EXTRATIME_BLUE &#x3D; Win the Blue team in extra time. It is thrown immediately when the ball enters the basket and makes it reach 2 points. 
   * @return event
  **/
  @ApiModelProperty(value = "Listed indicating the types of events: BEGIN = It occurs when the match begins (the referee releases the ball) END = It occurs when the referee makes the final whistle, players celebrating and ends the match PLAY = It happens when the match timer starts running PAUSE = Stop the match timer EXTRA_TIME = Mark that indicates that you will see extratime at the end of the minute when there has been a draw. POSSESSION_RED = Mark indicating that the Red team has possession. Just at the moment the player of the team in question catches the ball. Only marked at the beginning of the match POSSESSION_BLUE = Mark indicating that the Blue team has possession. Just at the moment the player of the team in question catches the ball. Only marked at the beginning of the match   END_POSSESION =Marker indicating that the 14 seconds of possession has ended. ANNOTATION = Marker indicating that the ball has been hit by any team STALLING = Marker indicating a stop for unexpected situations (player falls, etc.), that is, when a foul has not occurred or when there has been no ball court exit OUT_RED =  Marker indicating a ball court exit by the Red team OUT_BLUE = Marker indicating a ball court exit by the Blue team FREE_THROW_1 = A personal foul occurs within the triple area and produces 1 free throw FREE_THROW_2 = There is a personal foul outside the triple area and produces 2 free throws WIN_21_RED = Indicates when the Red team reaches 21. It is indicated by the referee WIN_21_BLUE = Indicates when the Blue team reaches 21. It is indicated by the referee WIN_TIME_RED = Indicates that the red team has won. To know if a team has won by points at the end of the minute without reaching 21, we will have to check that before this signal there is no WIN_21_RED WIN_TIME_BLUE = Indicates that the blue team has won. To know if a team has won by points at the end of the minute without reaching 21, we will have to check that before this signal there is no WIN_21_BLUE RES_TIME_DRAW = Indicates that there has been a draw. An EXTRA_TIME will always be launched WIN_EXTRATIME_RED = Win the Red team in extra time. It is thrown immediately when the ball enters the basket and makes it reach 2 points. WIN_EXTRATIME_BLUE = Win the Blue team in extra time. It is thrown immediately when the ball enters the basket and makes it reach 2 points. ")
  public EventEnum getEvent() {
    return event;
  }

  public void setEvent(EventEnum event) {
    this.event = event;
  }

  public BasketVideoHappening team(TeamEnum team) {
    this.team = team;
    return this;
  }

   /**
   * For ANNOTATION happenings, the team on which the happening takes place. For the rest, null Listed that identifies the team that performs an action: RED &#x3D; \&quot;Red Team. It is mapped in other parts of the system with TeamA BLUE &#x3D; Blue Team. It is mapped in other parts of the system with TeamB 
   * @return team
  **/
  @ApiModelProperty(value = "For ANNOTATION happenings, the team on which the happening takes place. For the rest, null Listed that identifies the team that performs an action: RED = \"Red Team. It is mapped in other parts of the system with TeamA BLUE = Blue Team. It is mapped in other parts of the system with TeamB ")
  public TeamEnum getTeam() {
    return team;
  }

  public void setTeam(TeamEnum team) {
    this.team = team;
  }

  public BasketVideoHappening playerId(Integer playerId) {
    this.playerId = playerId;
    return this;
  }

   /**
   * For ANNOTATION happenings, the player id on whom the happening takes place. For the rest, null. 
   * @return playerId
  **/
  @ApiModelProperty(value = "For ANNOTATION happenings, the player id on whom the happening takes place. For the rest, null. ")
  public Integer getPlayerId() {
    return playerId;
  }

  public void setPlayerId(Integer playerId) {
    this.playerId = playerId;
  }

  public BasketVideoHappening points(Integer points) {
    this.points = points;
    return this;
  }

   /**
   * For ANNOTATION happenings, points that occur in happining, can be 1 or 2. For the rest, null. 
   * @return points
  **/
  @ApiModelProperty(value = "For ANNOTATION happenings, points that occur in happining, can be 1 or 2. For the rest, null. ")
  public Integer getPoints() {
    return points;
  }

  public void setPoints(Integer points) {
    this.points = points;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasketVideoHappening basketVideoHappening = (BasketVideoHappening) o;
    return Objects.equals(this.time, basketVideoHappening.time) &&
        Objects.equals(this.event, basketVideoHappening.event) &&
        Objects.equals(this.team, basketVideoHappening.team) &&
        Objects.equals(this.playerId, basketVideoHappening.playerId) &&
        Objects.equals(this.points, basketVideoHappening.points);
  }

  @Override
  public int hashCode() {
    return Objects.hash(time, event, team, playerId, points);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasketVideoHappening {\n");
    
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    event: ").append(toIndentedString(event)).append("\n");
    sb.append("    team: ").append(toIndentedString(team)).append("\n");
    sb.append("    playerId: ").append(toIndentedString(playerId)).append("\n");
    sb.append("    points: ").append(toIndentedString(points)).append("\n");
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
