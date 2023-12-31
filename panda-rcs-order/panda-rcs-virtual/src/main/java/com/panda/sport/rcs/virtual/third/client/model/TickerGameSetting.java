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
 * Ticker configuration item in each of the states of the viewer. 
 */
@ApiModel(description = "Ticker configuration item in each of the states of the viewer. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class TickerGameSetting {
  /**
   * States in which the ticker can be displayed and configured. These are - PREAMBLE - moment in which the odds are being displayed - INGAME - estaod in which the reproduction of the event is being shown - RESULT - state in which the results are displayed. The states are defined by sequence order 
   */
  @JsonAdapter(KeyEnum.Adapter.class)
  public enum KeyEnum {
    PREAMBLE("PREAMBLE"),
    
    INGAME("INGAME"),
    
    RESULT("RESULT");

    private String value;

    KeyEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static KeyEnum fromValue(String text) {
      for (KeyEnum b : KeyEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<KeyEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final KeyEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public KeyEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return KeyEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("key")
  private KeyEnum key = null;

  @SerializedName("content")
  private List<String> content = null;

  @SerializedName("speed")
  private Integer speed = null;

  public TickerGameSetting key(KeyEnum key) {
    this.key = key;
    return this;
  }

   /**
   * States in which the ticker can be displayed and configured. These are - PREAMBLE - moment in which the odds are being displayed - INGAME - estaod in which the reproduction of the event is being shown - RESULT - state in which the results are displayed. The states are defined by sequence order 
   * @return key
  **/
  @ApiModelProperty(value = "States in which the ticker can be displayed and configured. These are - PREAMBLE - moment in which the odds are being displayed - INGAME - estaod in which the reproduction of the event is being shown - RESULT - state in which the results are displayed. The states are defined by sequence order ")
  public KeyEnum getKey() {
    return key;
  }

  public void setKey(KeyEnum key) {
    this.key = key;
  }

  public TickerGameSetting content(List<String> content) {
    this.content = content;
    return this;
  }

  public TickerGameSetting addContentItem(String contentItem) {
    if (this.content == null) {
      this.content = new ArrayList<String>();
    }
    this.content.add(contentItem);
    return this;
  }

   /**
   * Content that will be displayed in the ticker. This content will be defined by tag, and these are - text&#x3D;\&quot;&lt;custom_text&gt;\&quot;\&quot; -&gt; Displays a customized text in the ticker - ticket-limits -&gt; shows the limits of stake and paidout of the ticket - last-results -&gt; shows the last results - jackpot -&gt; shows the information of the last prize jackpot - megajackpot -&gt; shows the information of the last awarded megajackpot 
   * @return content
  **/
  @ApiModelProperty(value = "Content that will be displayed in the ticker. This content will be defined by tag, and these are - text=\"<custom_text>\"\" -> Displays a customized text in the ticker - ticket-limits -> shows the limits of stake and paidout of the ticket - last-results -> shows the last results - jackpot -> shows the information of the last prize jackpot - megajackpot -> shows the information of the last awarded megajackpot ")
  public List<String> getContent() {
    return content;
  }

  public void setContent(List<String> content) {
    this.content = content;
  }

  public TickerGameSetting speed(Integer speed) {
    this.speed = speed;
    return this;
  }

   /**
   * Speed at which the content in the ticker is displayed 
   * minimum: 100
   * maximum: 1000
   * @return speed
  **/
  @ApiModelProperty(value = "Speed at which the content in the ticker is displayed ")
  public Integer getSpeed() {
    return speed;
  }

  public void setSpeed(Integer speed) {
    this.speed = speed;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TickerGameSetting tickerGameSetting = (TickerGameSetting) o;
    return Objects.equals(this.key, tickerGameSetting.key) &&
        Objects.equals(this.content, tickerGameSetting.content) &&
        Objects.equals(this.speed, tickerGameSetting.speed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, content, speed);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TickerGameSetting {\n");
    
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    speed: ").append(toIndentedString(speed)).append("\n");
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

