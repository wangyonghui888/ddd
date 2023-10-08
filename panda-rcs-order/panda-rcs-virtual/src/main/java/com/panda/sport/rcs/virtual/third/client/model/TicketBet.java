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
import org.threeten.bp.OffsetDateTime;

/**
 * Information about Ticket bet. 
 */
@ApiModel(description = "Information about Ticket bet. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class TicketBet {
  @SerializedName("marketId")
  private String marketId = null;

  @SerializedName("marketName")
  private String marketName = null;

  @SerializedName("betId")
  private String betId = null;

  @SerializedName("betParam")
  private String betParam = null;

  @SerializedName("oddId")
  private String oddId = null;

  @SerializedName("oddName")
  private String oddName = null;

  @SerializedName("oddValue")
  private String oddValue = null;

  @SerializedName("stake")
  private Double stake = null;

  @SerializedName("cancelledStake")
  private Double cancelledStake = null;
  
  public TicketBet() {}

  public TicketBet(String marketId, String oddId, String oddValue , Double stake) {
	super();
	this.marketId = marketId;
	this.oddId = oddId;
	this.oddValue = oddValue;
	this.stake = stake;
}

/**
   * EventBlock types of all system   - OPEN   - WON   - CASHOUT   - LOST   - CANCEL   - N/A 
   */
  @JsonAdapter(StatusEnum.Adapter.class)
  public enum StatusEnum {
    OPEN("OPEN"),
    
    WON("WON"),
    
    CASHOUT("CASHOUT"),
    
    LOST("LOST"),
    
    CANCEL("CANCEL"),
    
    N_A("N/A");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static StatusEnum fromValue(String text) {
      for (StatusEnum b : StatusEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<StatusEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final StatusEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public StatusEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return StatusEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("status")
  private StatusEnum status = null;

  /**
   * Profit type for a ticketbet. This attribute is closely related to the asian handicap. Only set when Ticketbet.status &#x3D; WON - ALLWON - HALFWON - REFUND - HALFLOST - NONE 
   */
  @JsonAdapter(ProfitTypeEnum.Adapter.class)
  public enum ProfitTypeEnum {
    ALLWON("ALLWON"),
    
    HALFWON("HALFWON"),
    
    REFUND("REFUND"),
    
    HALFLOST("HALFLOST"),
    
    NONE("NONE");

    private String value;

    ProfitTypeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static ProfitTypeEnum fromValue(String text) {
      for (ProfitTypeEnum b : ProfitTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<ProfitTypeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final ProfitTypeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public ProfitTypeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return ProfitTypeEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("profitType")
  private ProfitTypeEnum profitType = null;

  @SerializedName("timeSolved")
  private OffsetDateTime timeSolved = null;

  public TicketBet marketId(String marketId) {
    this.marketId = marketId;
    return this;
  }

   /**
   * Get marketId
   * @return marketId
  **/
  @ApiModelProperty(value = "")
  public String getMarketId() {
    return marketId;
  }

  public void setMarketId(String marketId) {
    this.marketId = marketId;
  }

  public TicketBet marketName(String marketName) {
    this.marketName = marketName;
    return this;
  }

   /**
   * Get marketName
   * @return marketName
  **/
  @ApiModelProperty(value = "")
  public String getMarketName() {
    return marketName;
  }

  public void setMarketName(String marketName) {
    this.marketName = marketName;
  }

  public TicketBet betId(String betId) {
    this.betId = betId;
    return this;
  }

   /**
   * The Bet Id selected
   * @return betId
  **/
  @ApiModelProperty(value = "The Bet Id selected")
  public String getBetId() {
    return betId;
  }

  public void setBetId(String betId) {
    this.betId = betId;
  }

  public TicketBet betParam(String betParam) {
    this.betParam = betParam;
    return this;
  }

   /**
   * The Bet param selected if the Odd requires parameters as in a numbers games.
   * @return betParam
  **/
  @ApiModelProperty(value = "The Bet param selected if the Odd requires parameters as in a numbers games.")
  public String getBetParam() {
    return betParam;
  }

  public void setBetParam(String betParam) {
    this.betParam = betParam;
  }

  public TicketBet oddId(String oddId) {
    this.oddId = oddId;
    return this;
  }

   /**
   * Get oddId
   * @return oddId
  **/
  @ApiModelProperty(value = "")
  public String getOddId() {
    return oddId;
  }

  public void setOddId(String oddId) {
    this.oddId = oddId;
  }

  public TicketBet oddName(String oddName) {
    this.oddName = oddName;
    return this;
  }

   /**
   * Get oddName
   * @return oddName
  **/
  @ApiModelProperty(value = "")
  public String getOddName() {
    return oddName;
  }

  public void setOddName(String oddName) {
    this.oddName = oddName;
  }

  public TicketBet oddValue(String oddValue) {
    this.oddValue = oddValue;
    return this;
  }

   /**
   * Get oddValue
   * @return oddValue
  **/
  @ApiModelProperty(value = "")
  public String getOddValue() {
    return oddValue;
  }

  public void setOddValue(String oddValue) {
    this.oddValue = oddValue;
  }

  public TicketBet stake(Double stake) {
    this.stake = stake;
    return this;
  }

   /**
   * Get stake
   * @return stake
  **/
  @ApiModelProperty(value = "")
  public Double getStake() {
    return stake;
  }

  public void setStake(Double stake) {
    this.stake = stake;
  }

  public TicketBet cancelledStake(Double cancelledStake) {
    this.cancelledStake = cancelledStake;
    return this;
  }

   /**
   * Stake of cancelled ticket
   * @return cancelledStake
  **/
  @ApiModelProperty(value = "Stake of cancelled ticket")
  public Double getCancelledStake() {
    return cancelledStake;
  }

  public void setCancelledStake(Double cancelledStake) {
    this.cancelledStake = cancelledStake;
  }

  public TicketBet status(StatusEnum status) {
    this.status = status;
    return this;
  }

   /**
   * EventBlock types of all system   - OPEN   - WON   - CASHOUT   - LOST   - CANCEL   - N/A 
   * @return status
  **/
  @ApiModelProperty(value = "EventBlock types of all system   - OPEN   - WON   - CASHOUT   - LOST   - CANCEL   - N/A ")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public TicketBet profitType(ProfitTypeEnum profitType) {
    this.profitType = profitType;
    return this;
  }

   /**
   * Profit type for a ticketbet. This attribute is closely related to the asian handicap. Only set when Ticketbet.status &#x3D; WON - ALLWON - HALFWON - REFUND - HALFLOST - NONE 
   * @return profitType
  **/
  @ApiModelProperty(value = "Profit type for a ticketbet. This attribute is closely related to the asian handicap. Only set when Ticketbet.status = WON - ALLWON - HALFWON - REFUND - HALFLOST - NONE ")
  public ProfitTypeEnum getProfitType() {
    return profitType;
  }

  public void setProfitType(ProfitTypeEnum profitType) {
    this.profitType = profitType;
  }

  public TicketBet timeSolved(OffsetDateTime timeSolved) {
    this.timeSolved = timeSolved;
    return this;
  }

   /**
   * Get timeSolved
   * @return timeSolved
  **/
  @ApiModelProperty(value = "")
  public OffsetDateTime getTimeSolved() {
    return timeSolved;
  }

  public void setTimeSolved(OffsetDateTime timeSolved) {
    this.timeSolved = timeSolved;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TicketBet ticketBet = (TicketBet) o;
    return Objects.equals(this.marketId, ticketBet.marketId) &&
        Objects.equals(this.marketName, ticketBet.marketName) &&
        Objects.equals(this.betId, ticketBet.betId) &&
        Objects.equals(this.betParam, ticketBet.betParam) &&
        Objects.equals(this.oddId, ticketBet.oddId) &&
        Objects.equals(this.oddName, ticketBet.oddName) &&
        Objects.equals(this.oddValue, ticketBet.oddValue) &&
        Objects.equals(this.stake, ticketBet.stake) &&
        Objects.equals(this.cancelledStake, ticketBet.cancelledStake) &&
        Objects.equals(this.status, ticketBet.status) &&
        Objects.equals(this.profitType, ticketBet.profitType) &&
        Objects.equals(this.timeSolved, ticketBet.timeSolved);
  }

  @Override
  public int hashCode() {
    return Objects.hash(marketId, marketName, betId, betParam, oddId, oddName, oddValue, stake, cancelledStake, status, profitType, timeSolved);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TicketBet {\n");
    
    sb.append("    marketId: ").append(toIndentedString(marketId)).append("\n");
    sb.append("    marketName: ").append(toIndentedString(marketName)).append("\n");
    sb.append("    betId: ").append(toIndentedString(betId)).append("\n");
    sb.append("    betParam: ").append(toIndentedString(betParam)).append("\n");
    sb.append("    oddId: ").append(toIndentedString(oddId)).append("\n");
    sb.append("    oddName: ").append(toIndentedString(oddName)).append("\n");
    sb.append("    oddValue: ").append(toIndentedString(oddValue)).append("\n");
    sb.append("    stake: ").append(toIndentedString(stake)).append("\n");
    sb.append("    cancelledStake: ").append(toIndentedString(cancelledStake)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    profitType: ").append(toIndentedString(profitType)).append("\n");
    sb.append("    timeSolved: ").append(toIndentedString(timeSolved)).append("\n");
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

