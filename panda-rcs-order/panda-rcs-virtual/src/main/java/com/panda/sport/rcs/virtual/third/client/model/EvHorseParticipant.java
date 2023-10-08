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
import com.panda.sport.rcs.virtual.third.client.model.Participant;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The horse race participant information. 
 */
@ApiModel(description = "The horse race participant information. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EvHorseParticipant extends Participant {
  @SerializedName("name")
  private String name = null;

  @SerializedName("jockeyId")
  private Integer jockeyId = null;

  @SerializedName("jockey")
  private String jockey = null;

  @SerializedName("jockeyForecast")
  private List<String> jockeyForecast = null;

  @SerializedName("sex")
  private String sex = null;

  @SerializedName("color")
  private String color = null;

  @SerializedName("speed")
  private Float speed = null;

  @SerializedName("stamina")
  private Float stamina = null;

  @SerializedName("wins")
  private Float wins = null;

  @SerializedName("place")
  private Float place = null;

  @SerializedName("timeOff")
  private Integer timeOff = null;

  @SerializedName("art")
  private Float art = null;

  @SerializedName("ability")
  private Float ability = null;

  @SerializedName("star")
  private Integer star = null;

  @SerializedName("handicap")
  private Float handicap = null;

  @SerializedName("handicapPrev")
  private Float handicapPrev = null;

  @SerializedName("pace")
  private String pace = null;

  /**
   * Gets or Sets group
   */
  @JsonAdapter(GroupEnum.Adapter.class)
  public enum GroupEnum {
    NORMAL("NORMAL"),
    
    SLOW("SLOW");

    private String value;

    GroupEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static GroupEnum fromValue(String text) {
      for (GroupEnum b : GroupEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<GroupEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final GroupEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public GroupEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return GroupEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("group")
  private GroupEnum group = null;

  @SerializedName("oddStatic")
  private Float oddStatic = null;

  public EvHorseParticipant name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(value = "")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public EvHorseParticipant jockeyId(Integer jockeyId) {
    this.jockeyId = jockeyId;
    return this;
  }

   /**
   * Jockey Id in library resource 
   * @return jockeyId
  **/
  @ApiModelProperty(value = "Jockey Id in library resource ")
  public Integer getJockeyId() {
    return jockeyId;
  }

  public void setJockeyId(Integer jockeyId) {
    this.jockeyId = jockeyId;
  }

  public EvHorseParticipant jockey(String jockey) {
    this.jockey = jockey;
    return this;
  }

   /**
   * Jockey Name in library resource 
   * @return jockey
  **/
  @ApiModelProperty(value = "Jockey Name in library resource ")
  public String getJockey() {
    return jockey;
  }

  public void setJockey(String jockey) {
    this.jockey = jockey;
  }

  public EvHorseParticipant jockeyForecast(List<String> jockeyForecast) {
    this.jockeyForecast = jockeyForecast;
    return this;
  }

  public EvHorseParticipant addJockeyForecastItem(String jockeyForecastItem) {
    if (this.jockeyForecast == null) {
      this.jockeyForecast = new ArrayList<String>();
    }
    this.jockeyForecast.add(jockeyForecastItem);
    return this;
  }

   /**
   * Points system: First position - 4 points. Second position 2 points. Third position 1 point.     
   * @return jockeyForecast
  **/
  @ApiModelProperty(value = "Points system: First position - 4 points. Second position 2 points. Third position 1 point.     ")
  public List<String> getJockeyForecast() {
    return jockeyForecast;
  }

  public void setJockeyForecast(List<String> jockeyForecast) {
    this.jockeyForecast = jockeyForecast;
  }

  public EvHorseParticipant sex(String sex) {
    this.sex = sex;
    return this;
  }

   /**
   * Get sex
   * @return sex
  **/
  @ApiModelProperty(value = "")
  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public EvHorseParticipant color(String color) {
    this.color = color;
    return this;
  }

   /**
   * Get color
   * @return color
  **/
  @ApiModelProperty(value = "")
  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public EvHorseParticipant speed(Float speed) {
    this.speed = speed;
    return this;
  }

   /**
   * Get speed
   * @return speed
  **/
  @ApiModelProperty(value = "")
  public Float getSpeed() {
    return speed;
  }

  public void setSpeed(Float speed) {
    this.speed = speed;
  }

  public EvHorseParticipant stamina(Float stamina) {
    this.stamina = stamina;
    return this;
  }

   /**
   * Get stamina
   * @return stamina
  **/
  @ApiModelProperty(value = "")
  public Float getStamina() {
    return stamina;
  }

  public void setStamina(Float stamina) {
    this.stamina = stamina;
  }

  public EvHorseParticipant wins(Float wins) {
    this.wins = wins;
    return this;
  }

   /**
   * Get wins
   * @return wins
  **/
  @ApiModelProperty(value = "")
  public Float getWins() {
    return wins;
  }

  public void setWins(Float wins) {
    this.wins = wins;
  }

  public EvHorseParticipant place(Float place) {
    this.place = place;
    return this;
  }

   /**
   * Get place
   * @return place
  **/
  @ApiModelProperty(value = "")
  public Float getPlace() {
    return place;
  }

  public void setPlace(Float place) {
    this.place = place;
  }

  public EvHorseParticipant timeOff(Integer timeOff) {
    this.timeOff = timeOff;
    return this;
  }

   /**
   * Get timeOff
   * @return timeOff
  **/
  @ApiModelProperty(value = "")
  public Integer getTimeOff() {
    return timeOff;
  }

  public void setTimeOff(Integer timeOff) {
    this.timeOff = timeOff;
  }

  public EvHorseParticipant art(Float art) {
    this.art = art;
    return this;
  }

   /**
   * Get art
   * @return art
  **/
  @ApiModelProperty(value = "")
  public Float getArt() {
    return art;
  }

  public void setArt(Float art) {
    this.art = art;
  }

  public EvHorseParticipant ability(Float ability) {
    this.ability = ability;
    return this;
  }

   /**
   * Get ability
   * @return ability
  **/
  @ApiModelProperty(value = "")
  public Float getAbility() {
    return ability;
  }

  public void setAbility(Float ability) {
    this.ability = ability;
  }

  public EvHorseParticipant star(Integer star) {
    this.star = star;
    return this;
  }

   /**
   * Get star
   * @return star
  **/
  @ApiModelProperty(value = "")
  public Integer getStar() {
    return star;
  }

  public void setStar(Integer star) {
    this.star = star;
  }

  public EvHorseParticipant handicap(Float handicap) {
    this.handicap = handicap;
    return this;
  }

   /**
   * Get handicap
   * @return handicap
  **/
  @ApiModelProperty(value = "")
  public Float getHandicap() {
    return handicap;
  }

  public void setHandicap(Float handicap) {
    this.handicap = handicap;
  }

  public EvHorseParticipant handicapPrev(Float handicapPrev) {
    this.handicapPrev = handicapPrev;
    return this;
  }

   /**
   * Get handicapPrev
   * @return handicapPrev
  **/
  @ApiModelProperty(value = "")
  public Float getHandicapPrev() {
    return handicapPrev;
  }

  public void setHandicapPrev(Float handicapPrev) {
    this.handicapPrev = handicapPrev;
  }

  public EvHorseParticipant pace(String pace) {
    this.pace = pace;
    return this;
  }

   /**
   * Get pace
   * @return pace
  **/
  @ApiModelProperty(value = "")
  public String getPace() {
    return pace;
  }

  public void setPace(String pace) {
    this.pace = pace;
  }

  public EvHorseParticipant group(GroupEnum group) {
    this.group = group;
    return this;
  }

   /**
   * Get group
   * @return group
  **/
  @ApiModelProperty(value = "")
  public GroupEnum getGroup() {
    return group;
  }

  public void setGroup(GroupEnum group) {
    this.group = group;
  }

  public EvHorseParticipant oddStatic(Float oddStatic) {
    this.oddStatic = oddStatic;
    return this;
  }

   /**
   * static odd of the participant.
   * @return oddStatic
  **/
  @ApiModelProperty(value = "static odd of the participant.")
  public Float getOddStatic() {
    return oddStatic;
  }

  public void setOddStatic(Float oddStatic) {
    this.oddStatic = oddStatic;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EvHorseParticipant evHorseParticipant = (EvHorseParticipant) o;
    return Objects.equals(this.name, evHorseParticipant.name) &&
        Objects.equals(this.jockeyId, evHorseParticipant.jockeyId) &&
        Objects.equals(this.jockey, evHorseParticipant.jockey) &&
        Objects.equals(this.jockeyForecast, evHorseParticipant.jockeyForecast) &&
        Objects.equals(this.sex, evHorseParticipant.sex) &&
        Objects.equals(this.color, evHorseParticipant.color) &&
        Objects.equals(this.speed, evHorseParticipant.speed) &&
        Objects.equals(this.stamina, evHorseParticipant.stamina) &&
        Objects.equals(this.wins, evHorseParticipant.wins) &&
        Objects.equals(this.place, evHorseParticipant.place) &&
        Objects.equals(this.timeOff, evHorseParticipant.timeOff) &&
        Objects.equals(this.art, evHorseParticipant.art) &&
        Objects.equals(this.ability, evHorseParticipant.ability) &&
        Objects.equals(this.star, evHorseParticipant.star) &&
        Objects.equals(this.handicap, evHorseParticipant.handicap) &&
        Objects.equals(this.handicapPrev, evHorseParticipant.handicapPrev) &&
        Objects.equals(this.pace, evHorseParticipant.pace) &&
        Objects.equals(this.group, evHorseParticipant.group) &&
        Objects.equals(this.oddStatic, evHorseParticipant.oddStatic) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, jockeyId, jockey, jockeyForecast, sex, color, speed, stamina, wins, place, timeOff, art, ability, star, handicap, handicapPrev, pace, group, oddStatic, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EvHorseParticipant {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    jockeyId: ").append(toIndentedString(jockeyId)).append("\n");
    sb.append("    jockey: ").append(toIndentedString(jockey)).append("\n");
    sb.append("    jockeyForecast: ").append(toIndentedString(jockeyForecast)).append("\n");
    sb.append("    sex: ").append(toIndentedString(sex)).append("\n");
    sb.append("    color: ").append(toIndentedString(color)).append("\n");
    sb.append("    speed: ").append(toIndentedString(speed)).append("\n");
    sb.append("    stamina: ").append(toIndentedString(stamina)).append("\n");
    sb.append("    wins: ").append(toIndentedString(wins)).append("\n");
    sb.append("    place: ").append(toIndentedString(place)).append("\n");
    sb.append("    timeOff: ").append(toIndentedString(timeOff)).append("\n");
    sb.append("    art: ").append(toIndentedString(art)).append("\n");
    sb.append("    ability: ").append(toIndentedString(ability)).append("\n");
    sb.append("    star: ").append(toIndentedString(star)).append("\n");
    sb.append("    handicap: ").append(toIndentedString(handicap)).append("\n");
    sb.append("    handicapPrev: ").append(toIndentedString(handicapPrev)).append("\n");
    sb.append("    pace: ").append(toIndentedString(pace)).append("\n");
    sb.append("    group: ").append(toIndentedString(group)).append("\n");
    sb.append("    oddStatic: ").append(toIndentedString(oddStatic)).append("\n");
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

