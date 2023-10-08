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
import com.panda.sport.rcs.virtual.third.client.model.JackpotBonusScoringSettings;
import com.panda.sport.rcs.virtual.third.client.model.JackpotTypeSettings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JackpotBonusSettings
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class JackpotBonusSettings extends JackpotTypeSettings {
  /**
   * Reason  | Description --------| ----------------------------------------------------- Open    | The jackpot contributes to the status on sell. Solve   | The jackpot contributes to the status on solve. 
   */
  @JsonAdapter(ContributionModeEnum.Adapter.class)
  public enum ContributionModeEnum {
    OPEN("OPEN"),
    
    SOLVE("SOLVE");

    private String value;

    ContributionModeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static ContributionModeEnum fromValue(String text) {
      for (ContributionModeEnum b : ContributionModeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<ContributionModeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final ContributionModeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public ContributionModeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return ContributionModeEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("contributionMode")
  private ContributionModeEnum contributionMode = null;

  @SerializedName("expirationBonusTime")
  private Integer expirationBonusTime = null;

  @SerializedName("playerScoringTimeWindow")
  private Integer playerScoringTimeWindow = null;

  @SerializedName("levelPrize")
  private List<Double> levelPrize = null;

  @SerializedName("playerScoringSettings")
  private List<JackpotBonusScoringSettings> playerScoringSettings = null;

  public JackpotBonusSettings contributionMode(ContributionModeEnum contributionMode) {
    this.contributionMode = contributionMode;
    return this;
  }

   /**
   * Reason  | Description --------| ----------------------------------------------------- Open    | The jackpot contributes to the status on sell. Solve   | The jackpot contributes to the status on solve. 
   * @return contributionMode
  **/
  @ApiModelProperty(value = "Reason  | Description --------| ----------------------------------------------------- Open    | The jackpot contributes to the status on sell. Solve   | The jackpot contributes to the status on solve. ")
  public ContributionModeEnum getContributionMode() {
    return contributionMode;
  }

  public void setContributionMode(ContributionModeEnum contributionMode) {
    this.contributionMode = contributionMode;
  }

  public JackpotBonusSettings expirationBonusTime(Integer expirationBonusTime) {
    this.expirationBonusTime = expirationBonusTime;
    return this;
  }

   /**
   * The expiration time of the bonus. The time unit is minute. 
   * @return expirationBonusTime
  **/
  @ApiModelProperty(value = "The expiration time of the bonus. The time unit is minute. ")
  public Integer getExpirationBonusTime() {
    return expirationBonusTime;
  }

  public void setExpirationBonusTime(Integer expirationBonusTime) {
    this.expirationBonusTime = expirationBonusTime;
  }

  public JackpotBonusSettings playerScoringTimeWindow(Integer playerScoringTimeWindow) {
    this.playerScoringTimeWindow = playerScoringTimeWindow;
    return this;
  }

   /**
   * The time unit is day. It&#39;s the limit used to get the sum of the stake in a period.           
   * @return playerScoringTimeWindow
  **/
  @ApiModelProperty(value = "The time unit is day. It's the limit used to get the sum of the stake in a period.           ")
  public Integer getPlayerScoringTimeWindow() {
    return playerScoringTimeWindow;
  }

  public void setPlayerScoringTimeWindow(Integer playerScoringTimeWindow) {
    this.playerScoringTimeWindow = playerScoringTimeWindow;
  }

  public JackpotBonusSettings levelPrize(List<Double> levelPrize) {
    this.levelPrize = levelPrize;
    return this;
  }

  public JackpotBonusSettings addLevelPrizeItem(Double levelPrizeItem) {
    if (this.levelPrize == null) {
      this.levelPrize = new ArrayList<Double>();
    }
    this.levelPrize.add(levelPrizeItem);
    return this;
  }

   /**
   * It stores the prize amount for each level. 
   * @return levelPrize
  **/
  @ApiModelProperty(value = "It stores the prize amount for each level. ")
  public List<Double> getLevelPrize() {
    return levelPrize;
  }

  public void setLevelPrize(List<Double> levelPrize) {
    this.levelPrize = levelPrize;
  }

  public JackpotBonusSettings playerScoringSettings(List<JackpotBonusScoringSettings> playerScoringSettings) {
    this.playerScoringSettings = playerScoringSettings;
    return this;
  }

  public JackpotBonusSettings addPlayerScoringSettingsItem(JackpotBonusScoringSettings playerScoringSettingsItem) {
    if (this.playerScoringSettings == null) {
      this.playerScoringSettings = new ArrayList<JackpotBonusScoringSettings>();
    }
    this.playerScoringSettings.add(playerScoringSettingsItem);
    return this;
  }

   /**
   * It stores the N levels based on the stake of the player within the playerScoringTimeWindow 
   * @return playerScoringSettings
  **/
  @ApiModelProperty(value = "It stores the N levels based on the stake of the player within the playerScoringTimeWindow ")
  public List<JackpotBonusScoringSettings> getPlayerScoringSettings() {
    return playerScoringSettings;
  }

  public void setPlayerScoringSettings(List<JackpotBonusScoringSettings> playerScoringSettings) {
    this.playerScoringSettings = playerScoringSettings;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JackpotBonusSettings jackpotBonusSettings = (JackpotBonusSettings) o;
    return Objects.equals(this.contributionMode, jackpotBonusSettings.contributionMode) &&
        Objects.equals(this.expirationBonusTime, jackpotBonusSettings.expirationBonusTime) &&
        Objects.equals(this.playerScoringTimeWindow, jackpotBonusSettings.playerScoringTimeWindow) &&
        Objects.equals(this.levelPrize, jackpotBonusSettings.levelPrize) &&
        Objects.equals(this.playerScoringSettings, jackpotBonusSettings.playerScoringSettings) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contributionMode, expirationBonusTime, playerScoringTimeWindow, levelPrize, playerScoringSettings, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class JackpotBonusSettings {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    contributionMode: ").append(toIndentedString(contributionMode)).append("\n");
    sb.append("    expirationBonusTime: ").append(toIndentedString(expirationBonusTime)).append("\n");
    sb.append("    playerScoringTimeWindow: ").append(toIndentedString(playerScoringTimeWindow)).append("\n");
    sb.append("    levelPrize: ").append(toIndentedString(levelPrize)).append("\n");
    sb.append("    playerScoringSettings: ").append(toIndentedString(playerScoringSettings)).append("\n");
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

