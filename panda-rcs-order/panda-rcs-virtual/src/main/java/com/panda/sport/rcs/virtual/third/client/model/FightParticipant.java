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

/**
 * Fight participant information 
 */
@ApiModel(description = "Fight participant information ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class FightParticipant extends Participant {
  @SerializedName("name")
  private String name = null;

  @SerializedName("nation")
  private String nation = null;

  @SerializedName("age")
  private Integer age = null;

  @SerializedName("height")
  private String height = null;

  @SerializedName("weight")
  private Integer weight = null;

  @SerializedName("rating")
  private Float rating = null;

  public FightParticipant name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Name of the participant
   * @return name
  **/
  @ApiModelProperty(value = "Name of the participant")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public FightParticipant nation(String nation) {
    this.nation = nation;
    return this;
  }

   /**
   * Nation of the participant
   * @return nation
  **/
  @ApiModelProperty(value = "Nation of the participant")
  public String getNation() {
    return nation;
  }

  public void setNation(String nation) {
    this.nation = nation;
  }

  public FightParticipant age(Integer age) {
    this.age = age;
    return this;
  }

   /**
   * Age of the participant
   * @return age
  **/
  @ApiModelProperty(value = "Age of the participant")
  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public FightParticipant height(String height) {
    this.height = height;
    return this;
  }

   /**
   * Height of the participant
   * @return height
  **/
  @ApiModelProperty(value = "Height of the participant")
  public String getHeight() {
    return height;
  }

  public void setHeight(String height) {
    this.height = height;
  }

  public FightParticipant weight(Integer weight) {
    this.weight = weight;
    return this;
  }

   /**
   * Weight of the participant
   * @return weight
  **/
  @ApiModelProperty(value = "Weight of the participant")
  public Integer getWeight() {
    return weight;
  }

  public void setWeight(Integer weight) {
    this.weight = weight;
  }

  public FightParticipant rating(Float rating) {
    this.rating = rating;
    return this;
  }

   /**
   * Rating of the participant
   * @return rating
  **/
  @ApiModelProperty(value = "Rating of the participant")
  public Float getRating() {
    return rating;
  }

  public void setRating(Float rating) {
    this.rating = rating;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FightParticipant fightParticipant = (FightParticipant) o;
    return Objects.equals(this.name, fightParticipant.name) &&
        Objects.equals(this.nation, fightParticipant.nation) &&
        Objects.equals(this.age, fightParticipant.age) &&
        Objects.equals(this.height, fightParticipant.height) &&
        Objects.equals(this.weight, fightParticipant.weight) &&
        Objects.equals(this.rating, fightParticipant.rating) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, nation, age, height, weight, rating, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FightParticipant {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    nation: ").append(toIndentedString(nation)).append("\n");
    sb.append("    age: ").append(toIndentedString(age)).append("\n");
    sb.append("    height: ").append(toIndentedString(height)).append("\n");
    sb.append("    weight: ").append(toIndentedString(weight)).append("\n");
    sb.append("    rating: ").append(toIndentedString(rating)).append("\n");
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
