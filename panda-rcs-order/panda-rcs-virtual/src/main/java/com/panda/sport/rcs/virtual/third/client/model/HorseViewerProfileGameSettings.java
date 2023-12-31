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
import com.panda.sport.rcs.virtual.third.client.model.TickerGameSetting;
import com.panda.sport.rcs.virtual.third.client.model.ViewerProfileGameSettings;
import java.io.IOException;
import java.util.List;

/**
 * Horse Viewer Profile Game Settings 
 */
@ApiModel(description = "Horse Viewer Profile Game Settings ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class HorseViewerProfileGameSettings extends ViewerProfileGameSettings {
  /**
   * Amount of information to show in the layout of trifecta for 6 participants. Only applies to horse 6 viewers. If you select LEGACY, all combinations of trifecta junt or odds will be displayed.  If COMPACT is selected, the 18 TRICAST (HIGHT and LOW) of the event will be  displayed along with their odds. 
   */
  @JsonAdapter(TrifectaLayout6paticipantEnum.Adapter.class)
  public enum TrifectaLayout6paticipantEnum {
    LEGACY("LEGACY"),
    
    COMPACT("COMPACT");

    private String value;

    TrifectaLayout6paticipantEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static TrifectaLayout6paticipantEnum fromValue(String text) {
      for (TrifectaLayout6paticipantEnum b : TrifectaLayout6paticipantEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<TrifectaLayout6paticipantEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final TrifectaLayout6paticipantEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public TrifectaLayout6paticipantEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return TrifectaLayout6paticipantEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("trifectaLayout6paticipant")
  private TrifectaLayout6paticipantEnum trifectaLayout6paticipant = null;

  /**
   * Layout preamble selector for 12 participants. Only applies to horse 12 viewers. If you select LEGACY, You will see detailed information on all markets and market options. There is rotation of panels. If COMPACT is selected, only one view (without rotations) of markets and market options is shown. There is also a 3d preview of the participant. 
   */
  @JsonAdapter(PreambleLayoutHorse12Enum.Adapter.class)
  public enum PreambleLayoutHorse12Enum {
    LEGACY("LEGACY"),
    
    COMPACT("COMPACT");

    private String value;

    PreambleLayoutHorse12Enum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static PreambleLayoutHorse12Enum fromValue(String text) {
      for (PreambleLayoutHorse12Enum b : PreambleLayoutHorse12Enum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<PreambleLayoutHorse12Enum> {
      @Override
      public void write(final JsonWriter jsonWriter, final PreambleLayoutHorse12Enum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public PreambleLayoutHorse12Enum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return PreambleLayoutHorse12Enum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("preambleLayoutHorse12")
  private PreambleLayoutHorse12Enum preambleLayoutHorse12 = null;

  @SerializedName("showCurrency")
  private Boolean showCurrency = null;

  public HorseViewerProfileGameSettings trifectaLayout6paticipant(TrifectaLayout6paticipantEnum trifectaLayout6paticipant) {
    this.trifectaLayout6paticipant = trifectaLayout6paticipant;
    return this;
  }

   /**
   * Amount of information to show in the layout of trifecta for 6 participants. Only applies to horse 6 viewers. If you select LEGACY, all combinations of trifecta junt or odds will be displayed.  If COMPACT is selected, the 18 TRICAST (HIGHT and LOW) of the event will be  displayed along with their odds. 
   * @return trifectaLayout6paticipant
  **/
  @ApiModelProperty(value = "Amount of information to show in the layout of trifecta for 6 participants. Only applies to horse 6 viewers. If you select LEGACY, all combinations of trifecta junt or odds will be displayed.  If COMPACT is selected, the 18 TRICAST (HIGHT and LOW) of the event will be  displayed along with their odds. ")
  public TrifectaLayout6paticipantEnum getTrifectaLayout6paticipant() {
    return trifectaLayout6paticipant;
  }

  public void setTrifectaLayout6paticipant(TrifectaLayout6paticipantEnum trifectaLayout6paticipant) {
    this.trifectaLayout6paticipant = trifectaLayout6paticipant;
  }

  public HorseViewerProfileGameSettings preambleLayoutHorse12(PreambleLayoutHorse12Enum preambleLayoutHorse12) {
    this.preambleLayoutHorse12 = preambleLayoutHorse12;
    return this;
  }

   /**
   * Layout preamble selector for 12 participants. Only applies to horse 12 viewers. If you select LEGACY, You will see detailed information on all markets and market options. There is rotation of panels. If COMPACT is selected, only one view (without rotations) of markets and market options is shown. There is also a 3d preview of the participant. 
   * @return preambleLayoutHorse12
  **/
  @ApiModelProperty(value = "Layout preamble selector for 12 participants. Only applies to horse 12 viewers. If you select LEGACY, You will see detailed information on all markets and market options. There is rotation of panels. If COMPACT is selected, only one view (without rotations) of markets and market options is shown. There is also a 3d preview of the participant. ")
  public PreambleLayoutHorse12Enum getPreambleLayoutHorse12() {
    return preambleLayoutHorse12;
  }

  public void setPreambleLayoutHorse12(PreambleLayoutHorse12Enum preambleLayoutHorse12) {
    this.preambleLayoutHorse12 = preambleLayoutHorse12;
  }

  public HorseViewerProfileGameSettings showCurrency(Boolean showCurrency) {
    this.showCurrency = showCurrency;
    return this;
  }

   /**
   * In the preamble, ingame and results phases, the currency symbol will be displayed in each of the market options odd.
   * @return showCurrency
  **/
  @ApiModelProperty(value = "In the preamble, ingame and results phases, the currency symbol will be displayed in each of the market options odd.")
  public Boolean isShowCurrency() {
    return showCurrency;
  }

  public void setShowCurrency(Boolean showCurrency) {
    this.showCurrency = showCurrency;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HorseViewerProfileGameSettings horseViewerProfileGameSettings = (HorseViewerProfileGameSettings) o;
    return Objects.equals(this.trifectaLayout6paticipant, horseViewerProfileGameSettings.trifectaLayout6paticipant) &&
        Objects.equals(this.preambleLayoutHorse12, horseViewerProfileGameSettings.preambleLayoutHorse12) &&
        Objects.equals(this.showCurrency, horseViewerProfileGameSettings.showCurrency) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(trifectaLayout6paticipant, preambleLayoutHorse12, showCurrency, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HorseViewerProfileGameSettings {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    trifectaLayout6paticipant: ").append(toIndentedString(trifectaLayout6paticipant)).append("\n");
    sb.append("    preambleLayoutHorse12: ").append(toIndentedString(preambleLayoutHorse12)).append("\n");
    sb.append("    showCurrency: ").append(toIndentedString(showCurrency)).append("\n");
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

