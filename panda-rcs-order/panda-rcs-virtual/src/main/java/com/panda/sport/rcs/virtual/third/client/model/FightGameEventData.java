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
import com.panda.sport.rcs.virtual.third.client.model.GameEventData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Information about Fight events 
 */
@ApiModel(description = "Information about Fight events ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class FightGameEventData extends GameEventData {
  @SerializedName("victoryProb")
  private List<Double> victoryProb = null;

  public FightGameEventData victoryProb(List<Double> victoryProb) {
    this.victoryProb = victoryProb;
    return this;
  }

  public FightGameEventData addVictoryProbItem(Double victoryProbItem) {
    if (this.victoryProb == null) {
      this.victoryProb = new ArrayList<Double>();
    }
    this.victoryProb.add(victoryProbItem);
    return this;
  }

   /**
   * Victory combo market probability
   * @return victoryProb
  **/
  @ApiModelProperty(value = "Victory combo market probability")
  public List<Double> getVictoryProb() {
    return victoryProb;
  }

  public void setVictoryProb(List<Double> victoryProb) {
    this.victoryProb = victoryProb;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FightGameEventData fightGameEventData = (FightGameEventData) o;
    return Objects.equals(this.victoryProb, fightGameEventData.victoryProb) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(victoryProb, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FightGameEventData {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    victoryProb: ").append(toIndentedString(victoryProb)).append("\n");
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

