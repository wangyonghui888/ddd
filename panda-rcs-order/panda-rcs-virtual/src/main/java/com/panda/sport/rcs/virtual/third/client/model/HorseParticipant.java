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
import com.panda.sport.rcs.virtual.third.client.model.RaceParticipant;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The horse race participant information. 
 */
@ApiModel(description = "The horse race participant information. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class HorseParticipant extends RaceParticipant {
  @SerializedName("jockeySilkAssetId")
  private String jockeySilkAssetId = null;

  @SerializedName("jockey")
  private String jockey = null;

  @SerializedName("jockeyForecast")
  private List<String> jockeyForecast = null;

  @SerializedName("sex")
  private String sex = null;

  @SerializedName("color")
  private String color = null;

  @SerializedName("owner")
  private Integer owner = null;

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

  public HorseParticipant jockeySilkAssetId(String jockeySilkAssetId) {
    this.jockeySilkAssetId = jockeySilkAssetId;
    return this;
  }

   /**
   * Unique identifier of jockey silk asset. 
   * @return jockeySilkAssetId
  **/
  @ApiModelProperty(value = "Unique identifier of jockey silk asset. ")
  public String getJockeySilkAssetId() {
    return jockeySilkAssetId;
  }

  public void setJockeySilkAssetId(String jockeySilkAssetId) {
    this.jockeySilkAssetId = jockeySilkAssetId;
  }

  public HorseParticipant jockey(String jockey) {
    this.jockey = jockey;
    return this;
  }

   /**
   * Horse jockey
   * @return jockey
  **/
  @ApiModelProperty(value = "Horse jockey")
  public String getJockey() {
    return jockey;
  }

  public void setJockey(String jockey) {
    this.jockey = jockey;
  }

  public HorseParticipant jockeyForecast(List<String> jockeyForecast) {
    this.jockeyForecast = jockeyForecast;
    return this;
  }

  public HorseParticipant addJockeyForecastItem(String jockeyForecastItem) {
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
  @ApiModelProperty(value = "Points system: First position - 4 points. Second position 2 points. Third position 1 point. ")
  public List<String> getJockeyForecast() {
    return jockeyForecast;
  }

  public void setJockeyForecast(List<String> jockeyForecast) {
    this.jockeyForecast = jockeyForecast;
  }

  public HorseParticipant sex(String sex) {
    this.sex = sex;
    return this;
  }

   /**
   * Sex of the horse participant 
   * @return sex
  **/
  @ApiModelProperty(value = "Sex of the horse participant ")
  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public HorseParticipant color(String color) {
    this.color = color;
    return this;
  }

   /**
   * Color of the horse participant
   * @return color
  **/
  @ApiModelProperty(value = "Color of the horse participant")
  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public HorseParticipant owner(Integer owner) {
    this.owner = owner;
    return this;
  }

   /**
   * Owner of the horse participant
   * @return owner
  **/
  @ApiModelProperty(value = "Owner of the horse participant")
  public Integer getOwner() {
    return owner;
  }

  public void setOwner(Integer owner) {
    this.owner = owner;
  }

  public HorseParticipant speed(Float speed) {
    this.speed = speed;
    return this;
  }

   /**
   * Speed of the horse participant
   * @return speed
  **/
  @ApiModelProperty(value = "Speed of the horse participant")
  public Float getSpeed() {
    return speed;
  }

  public void setSpeed(Float speed) {
    this.speed = speed;
  }

  public HorseParticipant stamina(Float stamina) {
    this.stamina = stamina;
    return this;
  }

   /**
   * Stamina of the horse participant
   * @return stamina
  **/
  @ApiModelProperty(value = "Stamina of the horse participant")
  public Float getStamina() {
    return stamina;
  }

  public void setStamina(Float stamina) {
    this.stamina = stamina;
  }

  public HorseParticipant wins(Float wins) {
    this.wins = wins;
    return this;
  }

   /**
   * Wins of the horse participant
   * @return wins
  **/
  @ApiModelProperty(value = "Wins of the horse participant")
  public Float getWins() {
    return wins;
  }

  public void setWins(Float wins) {
    this.wins = wins;
  }

  public HorseParticipant place(Float place) {
    this.place = place;
    return this;
  }

   /**
   * Place of the horse participant
   * @return place
  **/
  @ApiModelProperty(value = "Place of the horse participant")
  public Float getPlace() {
    return place;
  }

  public void setPlace(Float place) {
    this.place = place;
  }

  public HorseParticipant timeOff(Integer timeOff) {
    this.timeOff = timeOff;
    return this;
  }

   /**
   * Time since the last race of this horse.
   * @return timeOff
  **/
  @ApiModelProperty(value = "Time since the last race of this horse.")
  public Integer getTimeOff() {
    return timeOff;
  }

  public void setTimeOff(Integer timeOff) {
    this.timeOff = timeOff;
  }

  public HorseParticipant art(Float art) {
    this.art = art;
    return this;
  }

   /**
   * Art of the horse participant 
   * @return art
  **/
  @ApiModelProperty(value = "Art of the horse participant ")
  public Float getArt() {
    return art;
  }

  public void setArt(Float art) {
    this.art = art;
  }

  public HorseParticipant ability(Float ability) {
    this.ability = ability;
    return this;
  }

   /**
   * Ability of the horse participant 
   * @return ability
  **/
  @ApiModelProperty(value = "Ability of the horse participant ")
  public Float getAbility() {
    return ability;
  }

  public void setAbility(Float ability) {
    this.ability = ability;
  }

  public HorseParticipant star(Integer star) {
    this.star = star;
    return this;
  }

   /**
   * Quality of the horse participant 
   * minimum: 1
   * maximum: 5
   * @return star
  **/
  @ApiModelProperty(value = "Quality of the horse participant ")
  public Integer getStar() {
    return star;
  }

  public void setStar(Integer star) {
    this.star = star;
  }

  public HorseParticipant handicap(Float handicap) {
    this.handicap = handicap;
    return this;
  }

   /**
   * Current Handicap of the horse participant
   * @return handicap
  **/
  @ApiModelProperty(value = "Current Handicap of the horse participant")
  public Float getHandicap() {
    return handicap;
  }

  public void setHandicap(Float handicap) {
    this.handicap = handicap;
  }

  public HorseParticipant handicapPrev(Float handicapPrev) {
    this.handicapPrev = handicapPrev;
    return this;
  }

   /**
   * Previois Handicap of the horse participant
   * @return handicapPrev
  **/
  @ApiModelProperty(value = "Previois Handicap of the horse participant")
  public Float getHandicapPrev() {
    return handicapPrev;
  }

  public void setHandicapPrev(Float handicapPrev) {
    this.handicapPrev = handicapPrev;
  }

  public HorseParticipant pace(String pace) {
    this.pace = pace;
    return this;
  }

   /**
   * Pace of the horse participant 
   * @return pace
  **/
  @ApiModelProperty(value = "Pace of the horse participant ")
  public String getPace() {
    return pace;
  }

  public void setPace(String pace) {
    this.pace = pace;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HorseParticipant horseParticipant = (HorseParticipant) o;
    return Objects.equals(this.jockeySilkAssetId, horseParticipant.jockeySilkAssetId) &&
        Objects.equals(this.jockey, horseParticipant.jockey) &&
        Objects.equals(this.jockeyForecast, horseParticipant.jockeyForecast) &&
        Objects.equals(this.sex, horseParticipant.sex) &&
        Objects.equals(this.color, horseParticipant.color) &&
        Objects.equals(this.owner, horseParticipant.owner) &&
        Objects.equals(this.speed, horseParticipant.speed) &&
        Objects.equals(this.stamina, horseParticipant.stamina) &&
        Objects.equals(this.wins, horseParticipant.wins) &&
        Objects.equals(this.place, horseParticipant.place) &&
        Objects.equals(this.timeOff, horseParticipant.timeOff) &&
        Objects.equals(this.art, horseParticipant.art) &&
        Objects.equals(this.ability, horseParticipant.ability) &&
        Objects.equals(this.star, horseParticipant.star) &&
        Objects.equals(this.handicap, horseParticipant.handicap) &&
        Objects.equals(this.handicapPrev, horseParticipant.handicapPrev) &&
        Objects.equals(this.pace, horseParticipant.pace) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jockeySilkAssetId, jockey, jockeyForecast, sex, color, owner, speed, stamina, wins, place, timeOff, art, ability, star, handicap, handicapPrev, pace, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HorseParticipant {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    jockeySilkAssetId: ").append(toIndentedString(jockeySilkAssetId)).append("\n");
    sb.append("    jockey: ").append(toIndentedString(jockey)).append("\n");
    sb.append("    jockeyForecast: ").append(toIndentedString(jockeyForecast)).append("\n");
    sb.append("    sex: ").append(toIndentedString(sex)).append("\n");
    sb.append("    color: ").append(toIndentedString(color)).append("\n");
    sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
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

