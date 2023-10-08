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
import com.panda.sport.rcs.virtual.third.client.model.JackpotTypeSettings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Declaration of jackpot configuration , with custom settings per currency , and JackpotType.  JackpotSettings are used to define, how the JackpotContribution of a ticket created on target entities , are calculated to contribute to a JackpotStatus.  JackpotStatus are stored according to keys defined in current settings  - jackpotId + targetId + currency + jackpotType  When evaluating a target instance, final targetId should be calculated according targetType 
 */
@ApiModel(description = "Declaration of jackpot configuration , with custom settings per currency , and JackpotType.  JackpotSettings are used to define, how the JackpotContribution of a ticket created on target entities , are calculated to contribute to a JackpotStatus.  JackpotStatus are stored according to keys defined in current settings  - jackpotId + targetId + currency + jackpotType  When evaluating a target instance, final targetId should be calculated according targetType ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class JackpotConfiguration {
  @SerializedName("jackpotId")
  private String jackpotId = null;

  @SerializedName("jackpotDesc")
  private String jackpotDesc = null;

  @SerializedName("targetId")
  private Integer targetId = null;

  /**
   * Defines how to calculate targetId, according to entity registering a ticket. Global, Local, Client,  Level1, Level2, Level3.    TargetType        | Description ------------------- | ---------------------------------   Global            | This is a custom jackpot.   Local             | The jackpot of the own entity.   Client            | The jackpot of the first client found.   Level1            | The jackpot of the father.   Level2            | The jackpot of the grandfather.   Level3            | The jackpot of the great grandfather. 
   */
  @JsonAdapter(TargetTypeEnum.Adapter.class)
  public enum TargetTypeEnum {
    GLOBAL("GLOBAL"),
    
    CLIENT("CLIENT"),
    
    LOCAL("LOCAL"),
    
    LEVEL1("LEVEL1"),
    
    LEVEL2("LEVEL2"),
    
    LEVEL3("LEVEL3");

    private String value;

    TargetTypeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static TargetTypeEnum fromValue(String text) {
      for (TargetTypeEnum b : TargetTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<TargetTypeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final TargetTypeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public TargetTypeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return TargetTypeEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("targetType")
  private TargetTypeEnum targetType = null;

  @SerializedName("playlistFilter")
  private List<Integer> playlistFilter = null;

  @SerializedName("playlistBlocked")
  private List<Integer> playlistBlocked = null;

  @SerializedName("status")
  private Boolean status = null;

  @SerializedName("jackpotTypeSettings")
  private List<JackpotTypeSettings> jackpotTypeSettings = null;

  public JackpotConfiguration jackpotId(String jackpotId) {
    this.jackpotId = jackpotId;
    return this;
  }

   /**
   * Unique Id of Jackpot slot. 
   * @return jackpotId
  **/
  @ApiModelProperty(required = true, value = "Unique Id of Jackpot slot. ")
  public String getJackpotId() {
    return jackpotId;
  }

  public void setJackpotId(String jackpotId) {
    this.jackpotId = jackpotId;
  }

  public JackpotConfiguration jackpotDesc(String jackpotDesc) {
    this.jackpotDesc = jackpotDesc;
    return this;
  }

   /**
   * Human description of current jackpot slot.
   * @return jackpotDesc
  **/
  @ApiModelProperty(value = "Human description of current jackpot slot.")
  public String getJackpotDesc() {
    return jackpotDesc;
  }

  public void setJackpotDesc(String jackpotDesc) {
    this.jackpotDesc = jackpotDesc;
  }

  public JackpotConfiguration targetId(Integer targetId) {
    this.targetId = targetId;
    return this;
  }

   /**
   * If Global, explicit targetId of JackpotStatus. If not global, it can optionally held the effective targetId, after evaluation of targetType. 
   * @return targetId
  **/
  @ApiModelProperty(value = "If Global, explicit targetId of JackpotStatus. If not global, it can optionally held the effective targetId, after evaluation of targetType. ")
  public Integer getTargetId() {
    return targetId;
  }

  public void setTargetId(Integer targetId) {
    this.targetId = targetId;
  }

  public JackpotConfiguration targetType(TargetTypeEnum targetType) {
    this.targetType = targetType;
    return this;
  }

   /**
   * Defines how to calculate targetId, according to entity registering a ticket. Global, Local, Client,  Level1, Level2, Level3.    TargetType        | Description ------------------- | ---------------------------------   Global            | This is a custom jackpot.   Local             | The jackpot of the own entity.   Client            | The jackpot of the first client found.   Level1            | The jackpot of the father.   Level2            | The jackpot of the grandfather.   Level3            | The jackpot of the great grandfather. 
   * @return targetType
  **/
  @ApiModelProperty(value = "Defines how to calculate targetId, according to entity registering a ticket. Global, Local, Client,  Level1, Level2, Level3.    TargetType        | Description ------------------- | ---------------------------------   Global            | This is a custom jackpot.   Local             | The jackpot of the own entity.   Client            | The jackpot of the first client found.   Level1            | The jackpot of the father.   Level2            | The jackpot of the grandfather.   Level3            | The jackpot of the great grandfather. ")
  public TargetTypeEnum getTargetType() {
    return targetType;
  }

  public void setTargetType(TargetTypeEnum targetType) {
    this.targetType = targetType;
  }

  public JackpotConfiguration playlistFilter(List<Integer> playlistFilter) {
    this.playlistFilter = playlistFilter;
    return this;
  }

  public JackpotConfiguration addPlaylistFilterItem(Integer playlistFilterItem) {
    if (this.playlistFilter == null) {
      this.playlistFilter = new ArrayList<Integer>();
    }
    this.playlistFilter.add(playlistFilterItem);
    return this;
  }

   /**
   * Filter of playlistId to apply jackpot. All if null.
   * @return playlistFilter
  **/
  @ApiModelProperty(value = "Filter of playlistId to apply jackpot. All if null.")
  public List<Integer> getPlaylistFilter() {
    return playlistFilter;
  }

  public void setPlaylistFilter(List<Integer> playlistFilter) {
    this.playlistFilter = playlistFilter;
  }

  public JackpotConfiguration playlistBlocked(List<Integer> playlistBlocked) {
    this.playlistBlocked = playlistBlocked;
    return this;
  }

  public JackpotConfiguration addPlaylistBlockedItem(Integer playlistBlockedItem) {
    if (this.playlistBlocked == null) {
      this.playlistBlocked = new ArrayList<Integer>();
    }
    this.playlistBlocked.add(playlistBlockedItem);
    return this;
  }

   /**
   * Explicit, excluded playlist of jackpot.
   * @return playlistBlocked
  **/
  @ApiModelProperty(value = "Explicit, excluded playlist of jackpot.")
  public List<Integer> getPlaylistBlocked() {
    return playlistBlocked;
  }

  public void setPlaylistBlocked(List<Integer> playlistBlocked) {
    this.playlistBlocked = playlistBlocked;
  }

  public JackpotConfiguration status(Boolean status) {
    this.status = status;
    return this;
  }

   /**
   * Enable / disable jackpot.
   * @return status
  **/
  @ApiModelProperty(value = "Enable / disable jackpot.")
  public Boolean isStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public JackpotConfiguration jackpotTypeSettings(List<JackpotTypeSettings> jackpotTypeSettings) {
    this.jackpotTypeSettings = jackpotTypeSettings;
    return this;
  }

  public JackpotConfiguration addJackpotTypeSettingsItem(JackpotTypeSettings jackpotTypeSettingsItem) {
    if (this.jackpotTypeSettings == null) {
      this.jackpotTypeSettings = new ArrayList<JackpotTypeSettings>();
    }
    this.jackpotTypeSettings.add(jackpotTypeSettingsItem);
    return this;
  }

   /**
   * Custom settigns for jackpotType. Not possible to add settings with same currency.
   * @return jackpotTypeSettings
  **/
  @ApiModelProperty(value = "Custom settigns for jackpotType. Not possible to add settings with same currency.")
  public List<JackpotTypeSettings> getJackpotTypeSettings() {
    return jackpotTypeSettings;
  }

  public void setJackpotTypeSettings(List<JackpotTypeSettings> jackpotTypeSettings) {
    this.jackpotTypeSettings = jackpotTypeSettings;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JackpotConfiguration jackpotConfiguration = (JackpotConfiguration) o;
    return Objects.equals(this.jackpotId, jackpotConfiguration.jackpotId) &&
        Objects.equals(this.jackpotDesc, jackpotConfiguration.jackpotDesc) &&
        Objects.equals(this.targetId, jackpotConfiguration.targetId) &&
        Objects.equals(this.targetType, jackpotConfiguration.targetType) &&
        Objects.equals(this.playlistFilter, jackpotConfiguration.playlistFilter) &&
        Objects.equals(this.playlistBlocked, jackpotConfiguration.playlistBlocked) &&
        Objects.equals(this.status, jackpotConfiguration.status) &&
        Objects.equals(this.jackpotTypeSettings, jackpotConfiguration.jackpotTypeSettings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jackpotId, jackpotDesc, targetId, targetType, playlistFilter, playlistBlocked, status, jackpotTypeSettings);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class JackpotConfiguration {\n");
    
    sb.append("    jackpotId: ").append(toIndentedString(jackpotId)).append("\n");
    sb.append("    jackpotDesc: ").append(toIndentedString(jackpotDesc)).append("\n");
    sb.append("    targetId: ").append(toIndentedString(targetId)).append("\n");
    sb.append("    targetType: ").append(toIndentedString(targetType)).append("\n");
    sb.append("    playlistFilter: ").append(toIndentedString(playlistFilter)).append("\n");
    sb.append("    playlistBlocked: ").append(toIndentedString(playlistBlocked)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    jackpotTypeSettings: ").append(toIndentedString(jackpotTypeSettings)).append("\n");
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
