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

/**
 * Configuration of Races Playlist 
 */
@ApiModel(description = "Configuration of Races Playlist ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class RacesFilter extends Filter {
  @SerializedName("maxProb")
  private Double maxProb = null;

  @SerializedName("minProb")
  private Double minProb = null;

  /**
   * Risk assumed in the participants selection
   */
  @JsonAdapter(RiskEnum.Adapter.class)
  public enum RiskEnum {
    NORMAL("NORMAL"),
    
    MEDIUM("MEDIUM"),
    
    HIGH("HIGH");

    private String value;

    RiskEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static RiskEnum fromValue(String text) {
      for (RiskEnum b : RiskEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<RiskEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final RiskEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public RiskEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return RiskEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("risk")
  private RiskEnum risk = null;

  /**
   * Specify participant selection strategies. Allows the following values:   - SIMULATE, choose participants randomly and simulate several races to calculate the probability.   - TABLE, choose participants randomly.   - FIXED_ODDS, choose participants randomly and take the probability from a resource file.   - STATIC, take participants and odds from a resource file. 
   */
  @JsonAdapter(RaceTypeEnum.Adapter.class)
  public enum RaceTypeEnum {
    SIMULATE("SIMULATE"),
    
    TABLE("TABLE"),
    
    FIXED_ODDS("FIXED_ODDS"),
    
    STATIC("STATIC");

    private String value;

    RaceTypeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static RaceTypeEnum fromValue(String text) {
      for (RaceTypeEnum b : RaceTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<RaceTypeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final RaceTypeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public RaceTypeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return RaceTypeEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("raceType")
  private RaceTypeEnum raceType = null;

  @SerializedName("contextParticipants")
  private String contextParticipants = null;

  @SerializedName("resourcePathNameParticipants")
  private String resourcePathNameParticipants = null;

  @SerializedName("contextVideo")
  private String contextVideo = null;

  @SerializedName("resourcePathNameVideo")
  private String resourcePathNameVideo = null;

  @SerializedName("numParticipants")
  private Integer numParticipants = null;

  @SerializedName("numPodium")
  private Integer numPodium = null;

  @SerializedName("libraryId")
  private String libraryId = null;

  /**
   * Indicates the type of content that will be displayed in the Ingame phase, that is, in the reproduction of the event. It is used to correctly calculate the mediaId and correctly manage the metadata. It can have 2 values; RENDER3D; the ingame of the playlist will be played using Aurora. The server will return the finalOrder and the mediaId (randomly selected from 90 pre-recorded sequences). Metadata will not be provided by the server. VIDEO; the server will provide everything necessary to mount the url (OCV and HLS) in the client part. The data to be provided is mediaExpireTime, mediaId (selected from a database which will have the identifier of all the videos) and mediaSignature. 
   */
  @JsonAdapter(InGameContentTypeEnum.Adapter.class)
  public enum InGameContentTypeEnum {
    VIDEOS("VIDEOS"),
    
    RENDER3D("RENDER3D");

    private String value;

    InGameContentTypeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static InGameContentTypeEnum fromValue(String text) {
      for (InGameContentTypeEnum b : InGameContentTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<InGameContentTypeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final InGameContentTypeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public InGameContentTypeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return InGameContentTypeEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("inGameContentType")
  private InGameContentTypeEnum inGameContentType = null;

  public RacesFilter maxProb(Double maxProb) {
    this.maxProb = maxProb;
    return this;
  }

   /**
   * Maximum probability
   * @return maxProb
  **/
  @ApiModelProperty(value = "Maximum probability")
  public Double getMaxProb() {
    return maxProb;
  }

  public void setMaxProb(Double maxProb) {
    this.maxProb = maxProb;
  }

  public RacesFilter minProb(Double minProb) {
    this.minProb = minProb;
    return this;
  }

   /**
   * Minimum probability
   * @return minProb
  **/
  @ApiModelProperty(value = "Minimum probability")
  public Double getMinProb() {
    return minProb;
  }

  public void setMinProb(Double minProb) {
    this.minProb = minProb;
  }

  public RacesFilter risk(RiskEnum risk) {
    this.risk = risk;
    return this;
  }

   /**
   * Risk assumed in the participants selection
   * @return risk
  **/
  @ApiModelProperty(value = "Risk assumed in the participants selection")
  public RiskEnum getRisk() {
    return risk;
  }

  public void setRisk(RiskEnum risk) {
    this.risk = risk;
  }

  public RacesFilter raceType(RaceTypeEnum raceType) {
    this.raceType = raceType;
    return this;
  }

   /**
   * Specify participant selection strategies. Allows the following values:   - SIMULATE, choose participants randomly and simulate several races to calculate the probability.   - TABLE, choose participants randomly.   - FIXED_ODDS, choose participants randomly and take the probability from a resource file.   - STATIC, take participants and odds from a resource file. 
   * @return raceType
  **/
  @ApiModelProperty(value = "Specify participant selection strategies. Allows the following values:   - SIMULATE, choose participants randomly and simulate several races to calculate the probability.   - TABLE, choose participants randomly.   - FIXED_ODDS, choose participants randomly and take the probability from a resource file.   - STATIC, take participants and odds from a resource file. ")
  public RaceTypeEnum getRaceType() {
    return raceType;
  }

  public void setRaceType(RaceTypeEnum raceType) {
    this.raceType = raceType;
  }

  public RacesFilter contextParticipants(String contextParticipants) {
    this.contextParticipants = contextParticipants;
    return this;
  }

   /**
   * Resource element used to load participants
   * @return contextParticipants
  **/
  @ApiModelProperty(value = "Resource element used to load participants")
  public String getContextParticipants() {
    return contextParticipants;
  }

  public void setContextParticipants(String contextParticipants) {
    this.contextParticipants = contextParticipants;
  }

  public RacesFilter resourcePathNameParticipants(String resourcePathNameParticipants) {
    this.resourcePathNameParticipants = resourcePathNameParticipants;
    return this;
  }

   /**
   * Route where is the resource to load participants.
   * @return resourcePathNameParticipants
  **/
  @ApiModelProperty(value = "Route where is the resource to load participants.")
  public String getResourcePathNameParticipants() {
    return resourcePathNameParticipants;
  }

  public void setResourcePathNameParticipants(String resourcePathNameParticipants) {
    this.resourcePathNameParticipants = resourcePathNameParticipants;
  }

  public RacesFilter contextVideo(String contextVideo) {
    this.contextVideo = contextVideo;
    return this;
  }

   /**
   * Identifier used to obtain the metadata (happenings) of a particular video. For Races there is a script called RaceVideo.json,  whose keys are the value set in this identifier. For Football, there is not yet a .json file and it is obtained directly from  the database. It is used by the server part. The allowed values can be;   - dog6   - motorbike6   - kart8   - speedway4   - dirttrack4   - horse6   - horse8 (not supported yet)   - horse10 (not supported yet)   - horse12   - horse12la 
   * @return contextVideo
  **/
  @ApiModelProperty(value = "Identifier used to obtain the metadata (happenings) of a particular video. For Races there is a script called RaceVideo.json,  whose keys are the value set in this identifier. For Football, there is not yet a .json file and it is obtained directly from  the database. It is used by the server part. The allowed values can be;   - dog6   - motorbike6   - kart8   - speedway4   - dirttrack4   - horse6   - horse8 (not supported yet)   - horse10 (not supported yet)   - horse12   - horse12la ")
  public String getContextVideo() {
    return contextVideo;
  }

  public void setContextVideo(String contextVideo) {
    this.contextVideo = contextVideo;
  }

  public RacesFilter resourcePathNameVideo(String resourcePathNameVideo) {
    this.resourcePathNameVideo = resourcePathNameVideo;
    return this;
  }

   /**
   * Route where is the resource to load videos
   * @return resourcePathNameVideo
  **/
  @ApiModelProperty(value = "Route where is the resource to load videos")
  public String getResourcePathNameVideo() {
    return resourcePathNameVideo;
  }

  public void setResourcePathNameVideo(String resourcePathNameVideo) {
    this.resourcePathNameVideo = resourcePathNameVideo;
  }

  public RacesFilter numParticipants(Integer numParticipants) {
    this.numParticipants = numParticipants;
    return this;
  }

   /**
   * Total number of participants who play the race 
   * @return numParticipants
  **/
  @ApiModelProperty(value = "Total number of participants who play the race ")
  public Integer getNumParticipants() {
    return numParticipants;
  }

  public void setNumParticipants(Integer numParticipants) {
    this.numParticipants = numParticipants;
  }

  public RacesFilter numPodium(Integer numPodium) {
    this.numPodium = numPodium;
    return this;
  }

   /**
   * Indicates the number of participants who will be able to take the podium. numPodium &lt; numParticipants &amp;&amp; numPodium &#x3D;&#x3D;&#x3D; EventResult.finalOutcome.lenght 
   * @return numPodium
  **/
  @ApiModelProperty(value = "Indicates the number of participants who will be able to take the podium. numPodium < numParticipants && numPodium === EventResult.finalOutcome.lenght ")
  public Integer getNumPodium() {
    return numPodium;
  }

  public void setNumPodium(Integer numPodium) {
    this.numPodium = numPodium;
  }

  public RacesFilter libraryId(String libraryId) {
    this.libraryId = libraryId;
    return this;
  }

   /**
   * Identify the specific game path to obtain the HLS videos. The values are;   - dog6 (dog for 6 participants)   - mt (motorbike)   - sp (speedway)   - sp2 (dirttrack)   - kt (kart)   - h63D (horse 3d for 6 participants, For more than 6 participants there are no videos hls) 
   * @return libraryId
  **/
  @ApiModelProperty(value = "Identify the specific game path to obtain the HLS videos. The values are;   - dog6 (dog for 6 participants)   - mt (motorbike)   - sp (speedway)   - sp2 (dirttrack)   - kt (kart)   - h63D (horse 3d for 6 participants, For more than 6 participants there are no videos hls) ")
  public String getLibraryId() {
    return libraryId;
  }

  public void setLibraryId(String libraryId) {
    this.libraryId = libraryId;
  }

  public RacesFilter inGameContentType(InGameContentTypeEnum inGameContentType) {
    this.inGameContentType = inGameContentType;
    return this;
  }

   /**
   * Indicates the type of content that will be displayed in the Ingame phase, that is, in the reproduction of the event. It is used to correctly calculate the mediaId and correctly manage the metadata. It can have 2 values; RENDER3D; the ingame of the playlist will be played using Aurora. The server will return the finalOrder and the mediaId (randomly selected from 90 pre-recorded sequences). Metadata will not be provided by the server. VIDEO; the server will provide everything necessary to mount the url (OCV and HLS) in the client part. The data to be provided is mediaExpireTime, mediaId (selected from a database which will have the identifier of all the videos) and mediaSignature. 
   * @return inGameContentType
  **/
  @ApiModelProperty(value = "Indicates the type of content that will be displayed in the Ingame phase, that is, in the reproduction of the event. It is used to correctly calculate the mediaId and correctly manage the metadata. It can have 2 values; RENDER3D; the ingame of the playlist will be played using Aurora. The server will return the finalOrder and the mediaId (randomly selected from 90 pre-recorded sequences). Metadata will not be provided by the server. VIDEO; the server will provide everything necessary to mount the url (OCV and HLS) in the client part. The data to be provided is mediaExpireTime, mediaId (selected from a database which will have the identifier of all the videos) and mediaSignature. ")
  public InGameContentTypeEnum getInGameContentType() {
    return inGameContentType;
  }

  public void setInGameContentType(InGameContentTypeEnum inGameContentType) {
    this.inGameContentType = inGameContentType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RacesFilter racesFilter = (RacesFilter) o;
    return Objects.equals(this.maxProb, racesFilter.maxProb) &&
        Objects.equals(this.minProb, racesFilter.minProb) &&
        Objects.equals(this.risk, racesFilter.risk) &&
        Objects.equals(this.raceType, racesFilter.raceType) &&
        Objects.equals(this.contextParticipants, racesFilter.contextParticipants) &&
        Objects.equals(this.resourcePathNameParticipants, racesFilter.resourcePathNameParticipants) &&
        Objects.equals(this.contextVideo, racesFilter.contextVideo) &&
        Objects.equals(this.resourcePathNameVideo, racesFilter.resourcePathNameVideo) &&
        Objects.equals(this.numParticipants, racesFilter.numParticipants) &&
        Objects.equals(this.numPodium, racesFilter.numPodium) &&
        Objects.equals(this.libraryId, racesFilter.libraryId) &&
        Objects.equals(this.inGameContentType, racesFilter.inGameContentType) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maxProb, minProb, risk, raceType, contextParticipants, resourcePathNameParticipants, contextVideo, resourcePathNameVideo, numParticipants, numPodium, libraryId, inGameContentType, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RacesFilter {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    maxProb: ").append(toIndentedString(maxProb)).append("\n");
    sb.append("    minProb: ").append(toIndentedString(minProb)).append("\n");
    sb.append("    risk: ").append(toIndentedString(risk)).append("\n");
    sb.append("    raceType: ").append(toIndentedString(raceType)).append("\n");
    sb.append("    contextParticipants: ").append(toIndentedString(contextParticipants)).append("\n");
    sb.append("    resourcePathNameParticipants: ").append(toIndentedString(resourcePathNameParticipants)).append("\n");
    sb.append("    contextVideo: ").append(toIndentedString(contextVideo)).append("\n");
    sb.append("    resourcePathNameVideo: ").append(toIndentedString(resourcePathNameVideo)).append("\n");
    sb.append("    numParticipants: ").append(toIndentedString(numParticipants)).append("\n");
    sb.append("    numPodium: ").append(toIndentedString(numPodium)).append("\n");
    sb.append("    libraryId: ").append(toIndentedString(libraryId)).append("\n");
    sb.append("    inGameContentType: ").append(toIndentedString(inGameContentType)).append("\n");
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

