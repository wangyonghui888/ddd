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
import com.panda.sport.rcs.virtual.third.client.model.EbkData;
import java.io.IOException;

/**
 * Internal Event block data for Baskets competition 
 */
@ApiModel(description = "Internal Event block data for Baskets competition ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class EbkDataBasket extends EbkData {
  @SerializedName("matchesGroupId")
  private Long matchesGroupId = null;

  /**
   * phase of the event block
   */
  @JsonAdapter(PhaseEnum.Adapter.class)
  public enum PhaseEnum {
    PREGAME("PREGAME"),
    
    INGAME("INGAME");

    private String value;

    PhaseEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static PhaseEnum fromValue(String text) {
      for (PhaseEnum b : PhaseEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<PhaseEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final PhaseEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public PhaseEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return PhaseEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("phase")
  private PhaseEnum phase = null;

  public EbkDataBasket matchesGroupId(Long matchesGroupId) {
    this.matchesGroupId = matchesGroupId;
    return this;
  }

   /**
   * Identifier basket matches group
   * @return matchesGroupId
  **/
  @ApiModelProperty(value = "Identifier basket matches group")
  public Long getMatchesGroupId() {
    return matchesGroupId;
  }

  public void setMatchesGroupId(Long matchesGroupId) {
    this.matchesGroupId = matchesGroupId;
  }

  public EbkDataBasket phase(PhaseEnum phase) {
    this.phase = phase;
    return this;
  }

   /**
   * phase of the event block
   * @return phase
  **/
  @ApiModelProperty(value = "phase of the event block")
  public PhaseEnum getPhase() {
    return phase;
  }

  public void setPhase(PhaseEnum phase) {
    this.phase = phase;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EbkDataBasket ebkDataBasket = (EbkDataBasket) o;
    return Objects.equals(this.matchesGroupId, ebkDataBasket.matchesGroupId) &&
        Objects.equals(this.phase, ebkDataBasket.phase) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(matchesGroupId, phase, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EbkDataBasket {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    matchesGroupId: ").append(toIndentedString(matchesGroupId)).append("\n");
    sb.append("    phase: ").append(toIndentedString(phase)).append("\n");
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

