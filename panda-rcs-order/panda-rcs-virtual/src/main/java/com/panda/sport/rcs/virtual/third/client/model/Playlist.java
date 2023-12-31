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
import com.panda.sport.rcs.virtual.third.client.model.Assets;
import com.panda.sport.rcs.virtual.third.client.model.Filter;
import com.panda.sport.rcs.virtual.third.client.model.GameType;
import com.panda.sport.rcs.virtual.third.client.model.Market;
import com.panda.sport.rcs.virtual.third.client.model.Participant;
import com.panda.sport.rcs.virtual.third.client.model.SchedulerConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Information about a game type playlist events. 
 */
@ApiModel(description = "Information about a game type playlist events. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class Playlist {
  @SerializedName("id")
  private Integer id = null;

  @SerializedName("gameType")
  private GameType gameType = null;

  @SerializedName("parentId")
  private Integer parentId = null;

  @SerializedName("path")
  private String path = null;

  /**
   * The mode define if the playlist is normal or a on-demand playlist 
   */
  @JsonAdapter(ModeEnum.Adapter.class)
  public enum ModeEnum {
    DISABLED("DISABLED"),
    
    SCHEDULED("SCHEDULED"),
    
    ONDEMAND("ONDEMAND"),
    
    LIVE("LIVE");

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

  @SerializedName("shortName")
  private String shortName = null;

  @SerializedName("filter")
  private Filter filter = null;

  @SerializedName("assets")
  private Assets assets = null;

  @SerializedName("marketOptionSize")
  private Integer marketOptionSize = null;

  @SerializedName("marketTemplates")
  private List<Market> marketTemplates = null;

  @SerializedName("participantTemplates")
  private List<Participant> participantTemplates = null;

  @SerializedName("description")
  private String description = null;

  @SerializedName("schedulerConfiguration")
  private List<SchedulerConfiguration> schedulerConfiguration = null;

  @SerializedName("allowedCountdown")
  private List<Integer> allowedCountdown = null;

  @SerializedName("allowedOffset")
  private List<Integer> allowedOffset = null;

  @SerializedName("scheduledWindow")
  private Integer scheduledWindow = null;

  public Playlist id(Integer id) {
    this.id = id;
    return this;
  }

   /**
   * Playlist Id. 
   * @return id
  **/
  @ApiModelProperty(value = "Playlist Id. ")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Playlist gameType(GameType gameType) {
    this.gameType = gameType;
    return this;
  }

   /**
   * Get gameType
   * @return gameType
  **/
  @ApiModelProperty(value = "")
  public GameType getGameType() {
    return gameType;
  }

  public void setGameType(GameType gameType) {
    this.gameType = gameType;
  }

  public Playlist parentId(Integer parentId) {
    this.parentId = parentId;
    return this;
  }

   /**
   * Parent Playlist Id in the Playlist Hierarchy System. This value will be NULL in case that the playlist is a GameType Main Playlist. 
   * @return parentId
  **/
  @ApiModelProperty(value = "Parent Playlist Id in the Playlist Hierarchy System. This value will be NULL in case that the playlist is a GameType Main Playlist. ")
  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  public Playlist path(String path) {
    this.path = path;
    return this;
  }

   /**
   * Contains the hierarchy path ids of the playlist. It begins by the GameType Main Playlist and finish in itself. The values are separated by commas. 
   * @return path
  **/
  @ApiModelProperty(value = "Contains the hierarchy path ids of the playlist. It begins by the GameType Main Playlist and finish in itself. The values are separated by commas. ")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Playlist mode(ModeEnum mode) {
    this.mode = mode;
    return this;
  }

   /**
   * The mode define if the playlist is normal or a on-demand playlist 
   * @return mode
  **/
  @ApiModelProperty(value = "The mode define if the playlist is normal or a on-demand playlist ")
  public ModeEnum getMode() {
    return mode;
  }

  public void setMode(ModeEnum mode) {
    this.mode = mode;
  }

  public Playlist shortName(String shortName) {
    this.shortName = shortName;
    return this;
  }

   /**
   * Get shortName
   * @return shortName
  **/
  @ApiModelProperty(value = "")
  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public Playlist filter(Filter filter) {
    this.filter = filter;
    return this;
  }

   /**
   * Get filter
   * @return filter
  **/
  @ApiModelProperty(value = "")
  public Filter getFilter() {
    return filter;
  }

  public void setFilter(Filter filter) {
    this.filter = filter;
  }

  public Playlist assets(Assets assets) {
    this.assets = assets;
    return this;
  }

   /**
   * Get assets
   * @return assets
  **/
  @ApiModelProperty(value = "")
  public Assets getAssets() {
    return assets;
  }

  public void setAssets(Assets assets) {
    this.assets = assets;
  }

  public Playlist marketOptionSize(Integer marketOptionSize) {
    this.marketOptionSize = marketOptionSize;
    return this;
  }

   /**
   * Gets the size of the market options to be able to compare when we get the size of the array of odds values in the eventData.          
   * @return marketOptionSize
  **/
  @ApiModelProperty(value = "Gets the size of the market options to be able to compare when we get the size of the array of odds values in the eventData.          ")
  public Integer getMarketOptionSize() {
    return marketOptionSize;
  }

  public void setMarketOptionSize(Integer marketOptionSize) {
    this.marketOptionSize = marketOptionSize;
  }

  public Playlist marketTemplates(List<Market> marketTemplates) {
    this.marketTemplates = marketTemplates;
    return this;
  }

  public Playlist addMarketTemplatesItem(Market marketTemplatesItem) {
    if (this.marketTemplates == null) {
      this.marketTemplates = new ArrayList<Market>();
    }
    this.marketTemplates.add(marketTemplatesItem);
    return this;
  }

   /**
   * get info markets for playlist. 
   * @return marketTemplates
  **/
  @ApiModelProperty(value = "get info markets for playlist. ")
  public List<Market> getMarketTemplates() {
    return marketTemplates;
  }

  public void setMarketTemplates(List<Market> marketTemplates) {
    this.marketTemplates = marketTemplates;
  }

  public Playlist participantTemplates(List<Participant> participantTemplates) {
    this.participantTemplates = participantTemplates;
    return this;
  }

  public Playlist addParticipantTemplatesItem(Participant participantTemplatesItem) {
    if (this.participantTemplates == null) {
      this.participantTemplates = new ArrayList<Participant>();
    }
    this.participantTemplates.add(participantTemplatesItem);
    return this;
  }

   /**
   * Get participantTemplates
   * @return participantTemplates
  **/
  @ApiModelProperty(value = "")
  public List<Participant> getParticipantTemplates() {
    return participantTemplates;
  }

  public void setParticipantTemplates(List<Participant> participantTemplates) {
    this.participantTemplates = participantTemplates;
  }

  public Playlist description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Playlist text description
   * @return description
  **/
  @ApiModelProperty(value = "Playlist text description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Playlist schedulerConfiguration(List<SchedulerConfiguration> schedulerConfiguration) {
    this.schedulerConfiguration = schedulerConfiguration;
    return this;
  }

  public Playlist addSchedulerConfigurationItem(SchedulerConfiguration schedulerConfigurationItem) {
    if (this.schedulerConfiguration == null) {
      this.schedulerConfiguration = new ArrayList<SchedulerConfiguration>();
    }
    this.schedulerConfiguration.add(schedulerConfigurationItem);
    return this;
  }

   /**
   * Manage scheduler configuration for this playlist 
   * @return schedulerConfiguration
  **/
  @ApiModelProperty(value = "Manage scheduler configuration for this playlist ")
  public List<SchedulerConfiguration> getSchedulerConfiguration() {
    return schedulerConfiguration;
  }

  public void setSchedulerConfiguration(List<SchedulerConfiguration> schedulerConfiguration) {
    this.schedulerConfiguration = schedulerConfiguration;
  }

  public Playlist allowedCountdown(List<Integer> allowedCountdown) {
    this.allowedCountdown = allowedCountdown;
    return this;
  }

  public Playlist addAllowedCountdownItem(Integer allowedCountdownItem) {
    if (this.allowedCountdown == null) {
      this.allowedCountdown = new ArrayList<Integer>();
    }
    this.allowedCountdown.add(allowedCountdownItem);
    return this;
  }

   /**
   * Allowed countdown list. The minimum allowed countdown will be used as the minimum countdown that can be used in the SchedulerConfiguration list. 
   * @return allowedCountdown
  **/
  @ApiModelProperty(value = "Allowed countdown list. The minimum allowed countdown will be used as the minimum countdown that can be used in the SchedulerConfiguration list. ")
  public List<Integer> getAllowedCountdown() {
    return allowedCountdown;
  }

  public void setAllowedCountdown(List<Integer> allowedCountdown) {
    this.allowedCountdown = allowedCountdown;
  }

  public Playlist allowedOffset(List<Integer> allowedOffset) {
    this.allowedOffset = allowedOffset;
    return this;
  }

  public Playlist addAllowedOffsetItem(Integer allowedOffsetItem) {
    if (this.allowedOffset == null) {
      this.allowedOffset = new ArrayList<Integer>();
    }
    this.allowedOffset.add(allowedOffsetItem);
    return this;
  }

   /**
   * Allowed offset list 
   * @return allowedOffset
  **/
  @ApiModelProperty(value = "Allowed offset list ")
  public List<Integer> getAllowedOffset() {
    return allowedOffset;
  }

  public void setAllowedOffset(List<Integer> allowedOffset) {
    this.allowedOffset = allowedOffset;
  }

  public Playlist scheduledWindow(Integer scheduledWindow) {
    this.scheduledWindow = scheduledWindow;
    return this;
  }

   /**
   * Number of minutes to ensure future scheduled events 
   * @return scheduledWindow
  **/
  @ApiModelProperty(value = "Number of minutes to ensure future scheduled events ")
  public Integer getScheduledWindow() {
    return scheduledWindow;
  }

  public void setScheduledWindow(Integer scheduledWindow) {
    this.scheduledWindow = scheduledWindow;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Playlist playlist = (Playlist) o;
    return Objects.equals(this.id, playlist.id) &&
        Objects.equals(this.gameType, playlist.gameType) &&
        Objects.equals(this.parentId, playlist.parentId) &&
        Objects.equals(this.path, playlist.path) &&
        Objects.equals(this.mode, playlist.mode) &&
        Objects.equals(this.shortName, playlist.shortName) &&
        Objects.equals(this.filter, playlist.filter) &&
        Objects.equals(this.assets, playlist.assets) &&
        Objects.equals(this.marketOptionSize, playlist.marketOptionSize) &&
        Objects.equals(this.marketTemplates, playlist.marketTemplates) &&
        Objects.equals(this.participantTemplates, playlist.participantTemplates) &&
        Objects.equals(this.description, playlist.description) &&
        Objects.equals(this.schedulerConfiguration, playlist.schedulerConfiguration) &&
        Objects.equals(this.allowedCountdown, playlist.allowedCountdown) &&
        Objects.equals(this.allowedOffset, playlist.allowedOffset) &&
        Objects.equals(this.scheduledWindow, playlist.scheduledWindow);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, gameType, parentId, path, mode, shortName, filter, assets, marketOptionSize, marketTemplates, participantTemplates, description, schedulerConfiguration, allowedCountdown, allowedOffset, scheduledWindow);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Playlist {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    gameType: ").append(toIndentedString(gameType)).append("\n");
    sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    mode: ").append(toIndentedString(mode)).append("\n");
    sb.append("    shortName: ").append(toIndentedString(shortName)).append("\n");
    sb.append("    filter: ").append(toIndentedString(filter)).append("\n");
    sb.append("    assets: ").append(toIndentedString(assets)).append("\n");
    sb.append("    marketOptionSize: ").append(toIndentedString(marketOptionSize)).append("\n");
    sb.append("    marketTemplates: ").append(toIndentedString(marketTemplates)).append("\n");
    sb.append("    participantTemplates: ").append(toIndentedString(participantTemplates)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    schedulerConfiguration: ").append(toIndentedString(schedulerConfiguration)).append("\n");
    sb.append("    allowedCountdown: ").append(toIndentedString(allowedCountdown)).append("\n");
    sb.append("    allowedOffset: ").append(toIndentedString(allowedOffset)).append("\n");
    sb.append("    scheduledWindow: ").append(toIndentedString(scheduledWindow)).append("\n");
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

