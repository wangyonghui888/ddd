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
import com.panda.sport.rcs.virtual.third.client.model.Filter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter for spin2win playlist 
 */
@ApiModel(description = "Filter for spin2win playlist ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class SnFilter extends Filter {
  /**
   * Selected the kind of spin between EUROPEAN or AMERICAN. 
   */
  @JsonAdapter(SpinTypeEnum.Adapter.class)
  public enum SpinTypeEnum {
    EUROPEAN("EUROPEAN"),
    
    AMERICAN("AMERICAN");

    private String value;

    SpinTypeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static SpinTypeEnum fromValue(String text) {
      for (SpinTypeEnum b : SpinTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<SpinTypeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final SpinTypeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public SpinTypeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return SpinTypeEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("spinType")
  private SpinTypeEnum spinType = null;

  /**
   * Selected the kind of spin between CLASSIC or DELUXE. 
   */
  @JsonAdapter(ModeEnum.Adapter.class)
  public enum ModeEnum {
    CLASSIC("CLASSIC"),
    
    DELUXE("DELUXE");

    private String value;

    ModeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static ModeEnum fromValue(String text) {
      for (ModeEnum b : ModeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<ModeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final ModeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public ModeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return ModeEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("mode")
  private ModeEnum mode = null;

  @SerializedName("generate")
  private List<Integer> generate = null;

  public SnFilter spinType(SpinTypeEnum spinType) {
    this.spinType = spinType;
    return this;
  }

   /**
   * Selected the kind of spin between EUROPEAN or AMERICAN. 
   * @return spinType
  **/
  @ApiModelProperty(value = "Selected the kind of spin between EUROPEAN or AMERICAN. ")
  public SpinTypeEnum getSpinType() {
    return spinType;
  }

  public void setSpinType(SpinTypeEnum spinType) {
    this.spinType = spinType;
  }

  public SnFilter mode(ModeEnum mode) {
    this.mode = mode;
    return this;
  }

   /**
   * Selected the kind of spin between CLASSIC or DELUXE. 
   * @return mode
  **/
  @ApiModelProperty(value = "Selected the kind of spin between CLASSIC or DELUXE. ")
  public ModeEnum getMode() {
    return mode;
  }

  public void setMode(ModeEnum mode) {
    this.mode = mode;
  }

  public SnFilter generate(List<Integer> generate) {
    this.generate = generate;
    return this;
  }

  public SnFilter addGenerateItem(Integer generateItem) {
    if (this.generate == null) {
      this.generate = new ArrayList<Integer>();
    }
    this.generate.add(generateItem);
    return this;
  }

   /**
   * Result interval 
   * @return generate
  **/
  @ApiModelProperty(value = "Result interval ")
  public List<Integer> getGenerate() {
    return generate;
  }

  public void setGenerate(List<Integer> generate) {
    this.generate = generate;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SnFilter snFilter = (SnFilter) o;
    return Objects.equals(this.spinType, snFilter.spinType) &&
        Objects.equals(this.mode, snFilter.mode) &&
        Objects.equals(this.generate, snFilter.generate) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(spinType, mode, generate, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SnFilter {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    spinType: ").append(toIndentedString(spinType)).append("\n");
    sb.append("    mode: ").append(toIndentedString(mode)).append("\n");
    sb.append("    generate: ").append(toIndentedString(generate)).append("\n");
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

