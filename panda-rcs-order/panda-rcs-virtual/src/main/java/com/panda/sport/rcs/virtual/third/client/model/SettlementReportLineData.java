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

/**
 * Object to define account data line for each playlist
 */
@ApiModel(description = "Object to define account data line for each playlist")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class SettlementReportLineData {
  @SerializedName("playlistId")
  private String playlistId = null;

  @SerializedName("stake")
  private Double stake = 0.0d;

  @SerializedName("stakeCancelled")
  private Double stakeCancelled = 0.0d;

  @SerializedName("directCashIn")
  private Double directCashIn = 0.0d;

  @SerializedName("paidOut")
  private Double paidOut = 0.0d;

  @SerializedName("jackpotPaidOut")
  private Double jackpotPaidOut = 0.0d;

  @SerializedName("megaJackpotPaidOut")
  private Double megaJackpotPaidOut = 0.0d;

  @SerializedName("bonusPaidOut")
  private Double bonusPaidOut = 0.0d;

  @SerializedName("cappedPaidOut")
  private Double cappedPaidOut = 0.0d;

  @SerializedName("directCashOut")
  private Double directCashOut = 0.0d;

  @SerializedName("won")
  private Double won = 0.0d;

  @SerializedName("jackpotWon")
  private Double jackpotWon = 0.0d;

  @SerializedName("megaJackpotWon")
  private Double megaJackpotWon = 0.0d;

  @SerializedName("bonusWon")
  private Double bonusWon = 0.0d;

  @SerializedName("cappedWon")
  private Double cappedWon = 0.0d;

  @SerializedName("stakeTaxes")
  private Double stakeTaxes = 0.0d;

  @SerializedName("stakeCancelledTaxes")
  private Double stakeCancelledTaxes = 0.0d;

  @SerializedName("paidOutTaxes")
  private Double paidOutTaxes = 0.0d;

  @SerializedName("jackpotContribution")
  private Double jackpotContribution = 0.0d;

  @SerializedName("megaJackpotContribution")
  private Double megaJackpotContribution = 0.0d;

  public SettlementReportLineData playlistId(String playlistId) {
    this.playlistId = playlistId;
    return this;
  }

   /**
   * Playlist Id.
   * @return playlistId
  **/
  @ApiModelProperty(value = "Playlist Id.")
  public String getPlaylistId() {
    return playlistId;
  }

  public void setPlaylistId(String playlistId) {
    this.playlistId = playlistId;
  }

  public SettlementReportLineData stake(Double stake) {
    this.stake = stake;
    return this;
  }

   /**
   * Stake.
   * @return stake
  **/
  @ApiModelProperty(value = "Stake.")
  public Double getStake() {
    return stake;
  }

  public void setStake(Double stake) {
    this.stake = stake;
  }

  public SettlementReportLineData stakeCancelled(Double stakeCancelled) {
    this.stakeCancelled = stakeCancelled;
    return this;
  }

   /**
   * Stake.
   * @return stakeCancelled
  **/
  @ApiModelProperty(value = "Stake.")
  public Double getStakeCancelled() {
    return stakeCancelled;
  }

  public void setStakeCancelled(Double stakeCancelled) {
    this.stakeCancelled = stakeCancelled;
  }

  public SettlementReportLineData directCashIn(Double directCashIn) {
    this.directCashIn = directCashIn;
    return this;
  }

   /**
   * Direct cash in.
   * @return directCashIn
  **/
  @ApiModelProperty(value = "Direct cash in.")
  public Double getDirectCashIn() {
    return directCashIn;
  }

  public void setDirectCashIn(Double directCashIn) {
    this.directCashIn = directCashIn;
  }

  public SettlementReportLineData paidOut(Double paidOut) {
    this.paidOut = paidOut;
    return this;
  }

   /**
   * Won paid.
   * @return paidOut
  **/
  @ApiModelProperty(value = "Won paid.")
  public Double getPaidOut() {
    return paidOut;
  }

  public void setPaidOut(Double paidOut) {
    this.paidOut = paidOut;
  }

  public SettlementReportLineData jackpotPaidOut(Double jackpotPaidOut) {
    this.jackpotPaidOut = jackpotPaidOut;
    return this;
  }

   /**
   * Jackpot paid.
   * @return jackpotPaidOut
  **/
  @ApiModelProperty(value = "Jackpot paid.")
  public Double getJackpotPaidOut() {
    return jackpotPaidOut;
  }

  public void setJackpotPaidOut(Double jackpotPaidOut) {
    this.jackpotPaidOut = jackpotPaidOut;
  }

  public SettlementReportLineData megaJackpotPaidOut(Double megaJackpotPaidOut) {
    this.megaJackpotPaidOut = megaJackpotPaidOut;
    return this;
  }

   /**
   * Mega Jackpot paid.
   * @return megaJackpotPaidOut
  **/
  @ApiModelProperty(value = "Mega Jackpot paid.")
  public Double getMegaJackpotPaidOut() {
    return megaJackpotPaidOut;
  }

  public void setMegaJackpotPaidOut(Double megaJackpotPaidOut) {
    this.megaJackpotPaidOut = megaJackpotPaidOut;
  }

  public SettlementReportLineData bonusPaidOut(Double bonusPaidOut) {
    this.bonusPaidOut = bonusPaidOut;
    return this;
  }

   /**
   * Bonus paid.
   * @return bonusPaidOut
  **/
  @ApiModelProperty(value = "Bonus paid.")
  public Double getBonusPaidOut() {
    return bonusPaidOut;
  }

  public void setBonusPaidOut(Double bonusPaidOut) {
    this.bonusPaidOut = bonusPaidOut;
  }

  public SettlementReportLineData cappedPaidOut(Double cappedPaidOut) {
    this.cappedPaidOut = cappedPaidOut;
    return this;
  }

   /**
   * Capped paid amount.
   * @return cappedPaidOut
  **/
  @ApiModelProperty(value = "Capped paid amount.")
  public Double getCappedPaidOut() {
    return cappedPaidOut;
  }

  public void setCappedPaidOut(Double cappedPaidOut) {
    this.cappedPaidOut = cappedPaidOut;
  }

  public SettlementReportLineData directCashOut(Double directCashOut) {
    this.directCashOut = directCashOut;
    return this;
  }

   /**
   * Direct cash out.
   * @return directCashOut
  **/
  @ApiModelProperty(value = "Direct cash out.")
  public Double getDirectCashOut() {
    return directCashOut;
  }

  public void setDirectCashOut(Double directCashOut) {
    this.directCashOut = directCashOut;
  }

  public SettlementReportLineData won(Double won) {
    this.won = won;
    return this;
  }

   /**
   * Won.
   * @return won
  **/
  @ApiModelProperty(value = "Won.")
  public Double getWon() {
    return won;
  }

  public void setWon(Double won) {
    this.won = won;
  }

  public SettlementReportLineData jackpotWon(Double jackpotWon) {
    this.jackpotWon = jackpotWon;
    return this;
  }

   /**
   * Jackpot won.
   * @return jackpotWon
  **/
  @ApiModelProperty(value = "Jackpot won.")
  public Double getJackpotWon() {
    return jackpotWon;
  }

  public void setJackpotWon(Double jackpotWon) {
    this.jackpotWon = jackpotWon;
  }

  public SettlementReportLineData megaJackpotWon(Double megaJackpotWon) {
    this.megaJackpotWon = megaJackpotWon;
    return this;
  }

   /**
   * Mega Jackpot won.
   * @return megaJackpotWon
  **/
  @ApiModelProperty(value = "Mega Jackpot won.")
  public Double getMegaJackpotWon() {
    return megaJackpotWon;
  }

  public void setMegaJackpotWon(Double megaJackpotWon) {
    this.megaJackpotWon = megaJackpotWon;
  }

  public SettlementReportLineData bonusWon(Double bonusWon) {
    this.bonusWon = bonusWon;
    return this;
  }

   /**
   * Bonus won.
   * @return bonusWon
  **/
  @ApiModelProperty(value = "Bonus won.")
  public Double getBonusWon() {
    return bonusWon;
  }

  public void setBonusWon(Double bonusWon) {
    this.bonusWon = bonusWon;
  }

  public SettlementReportLineData cappedWon(Double cappedWon) {
    this.cappedWon = cappedWon;
    return this;
  }

   /**
   * Capped Won amount.
   * @return cappedWon
  **/
  @ApiModelProperty(value = "Capped Won amount.")
  public Double getCappedWon() {
    return cappedWon;
  }

  public void setCappedWon(Double cappedWon) {
    this.cappedWon = cappedWon;
  }

  public SettlementReportLineData stakeTaxes(Double stakeTaxes) {
    this.stakeTaxes = stakeTaxes;
    return this;
  }

   /**
   * Stake taxes.
   * @return stakeTaxes
  **/
  @ApiModelProperty(value = "Stake taxes.")
  public Double getStakeTaxes() {
    return stakeTaxes;
  }

  public void setStakeTaxes(Double stakeTaxes) {
    this.stakeTaxes = stakeTaxes;
  }

  public SettlementReportLineData stakeCancelledTaxes(Double stakeCancelledTaxes) {
    this.stakeCancelledTaxes = stakeCancelledTaxes;
    return this;
  }

   /**
   * Stake Cancelled taxes.
   * @return stakeCancelledTaxes
  **/
  @ApiModelProperty(value = "Stake Cancelled taxes.")
  public Double getStakeCancelledTaxes() {
    return stakeCancelledTaxes;
  }

  public void setStakeCancelledTaxes(Double stakeCancelledTaxes) {
    this.stakeCancelledTaxes = stakeCancelledTaxes;
  }

  public SettlementReportLineData paidOutTaxes(Double paidOutTaxes) {
    this.paidOutTaxes = paidOutTaxes;
    return this;
  }

   /**
   * Won taxes retained.
   * @return paidOutTaxes
  **/
  @ApiModelProperty(value = "Won taxes retained.")
  public Double getPaidOutTaxes() {
    return paidOutTaxes;
  }

  public void setPaidOutTaxes(Double paidOutTaxes) {
    this.paidOutTaxes = paidOutTaxes;
  }

  public SettlementReportLineData jackpotContribution(Double jackpotContribution) {
    this.jackpotContribution = jackpotContribution;
    return this;
  }

   /**
   * Jackpot contribution.
   * @return jackpotContribution
  **/
  @ApiModelProperty(value = "Jackpot contribution.")
  public Double getJackpotContribution() {
    return jackpotContribution;
  }

  public void setJackpotContribution(Double jackpotContribution) {
    this.jackpotContribution = jackpotContribution;
  }

  public SettlementReportLineData megaJackpotContribution(Double megaJackpotContribution) {
    this.megaJackpotContribution = megaJackpotContribution;
    return this;
  }

   /**
   * Mega Jackpot contribution.
   * @return megaJackpotContribution
  **/
  @ApiModelProperty(value = "Mega Jackpot contribution.")
  public Double getMegaJackpotContribution() {
    return megaJackpotContribution;
  }

  public void setMegaJackpotContribution(Double megaJackpotContribution) {
    this.megaJackpotContribution = megaJackpotContribution;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SettlementReportLineData settlementReportLineData = (SettlementReportLineData) o;
    return Objects.equals(this.playlistId, settlementReportLineData.playlistId) &&
        Objects.equals(this.stake, settlementReportLineData.stake) &&
        Objects.equals(this.stakeCancelled, settlementReportLineData.stakeCancelled) &&
        Objects.equals(this.directCashIn, settlementReportLineData.directCashIn) &&
        Objects.equals(this.paidOut, settlementReportLineData.paidOut) &&
        Objects.equals(this.jackpotPaidOut, settlementReportLineData.jackpotPaidOut) &&
        Objects.equals(this.megaJackpotPaidOut, settlementReportLineData.megaJackpotPaidOut) &&
        Objects.equals(this.bonusPaidOut, settlementReportLineData.bonusPaidOut) &&
        Objects.equals(this.cappedPaidOut, settlementReportLineData.cappedPaidOut) &&
        Objects.equals(this.directCashOut, settlementReportLineData.directCashOut) &&
        Objects.equals(this.won, settlementReportLineData.won) &&
        Objects.equals(this.jackpotWon, settlementReportLineData.jackpotWon) &&
        Objects.equals(this.megaJackpotWon, settlementReportLineData.megaJackpotWon) &&
        Objects.equals(this.bonusWon, settlementReportLineData.bonusWon) &&
        Objects.equals(this.cappedWon, settlementReportLineData.cappedWon) &&
        Objects.equals(this.stakeTaxes, settlementReportLineData.stakeTaxes) &&
        Objects.equals(this.stakeCancelledTaxes, settlementReportLineData.stakeCancelledTaxes) &&
        Objects.equals(this.paidOutTaxes, settlementReportLineData.paidOutTaxes) &&
        Objects.equals(this.jackpotContribution, settlementReportLineData.jackpotContribution) &&
        Objects.equals(this.megaJackpotContribution, settlementReportLineData.megaJackpotContribution);
  }

  @Override
  public int hashCode() {
    return Objects.hash(playlistId, stake, stakeCancelled, directCashIn, paidOut, jackpotPaidOut, megaJackpotPaidOut, bonusPaidOut, cappedPaidOut, directCashOut, won, jackpotWon, megaJackpotWon, bonusWon, cappedWon, stakeTaxes, stakeCancelledTaxes, paidOutTaxes, jackpotContribution, megaJackpotContribution);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SettlementReportLineData {\n");
    
    sb.append("    playlistId: ").append(toIndentedString(playlistId)).append("\n");
    sb.append("    stake: ").append(toIndentedString(stake)).append("\n");
    sb.append("    stakeCancelled: ").append(toIndentedString(stakeCancelled)).append("\n");
    sb.append("    directCashIn: ").append(toIndentedString(directCashIn)).append("\n");
    sb.append("    paidOut: ").append(toIndentedString(paidOut)).append("\n");
    sb.append("    jackpotPaidOut: ").append(toIndentedString(jackpotPaidOut)).append("\n");
    sb.append("    megaJackpotPaidOut: ").append(toIndentedString(megaJackpotPaidOut)).append("\n");
    sb.append("    bonusPaidOut: ").append(toIndentedString(bonusPaidOut)).append("\n");
    sb.append("    cappedPaidOut: ").append(toIndentedString(cappedPaidOut)).append("\n");
    sb.append("    directCashOut: ").append(toIndentedString(directCashOut)).append("\n");
    sb.append("    won: ").append(toIndentedString(won)).append("\n");
    sb.append("    jackpotWon: ").append(toIndentedString(jackpotWon)).append("\n");
    sb.append("    megaJackpotWon: ").append(toIndentedString(megaJackpotWon)).append("\n");
    sb.append("    bonusWon: ").append(toIndentedString(bonusWon)).append("\n");
    sb.append("    cappedWon: ").append(toIndentedString(cappedWon)).append("\n");
    sb.append("    stakeTaxes: ").append(toIndentedString(stakeTaxes)).append("\n");
    sb.append("    stakeCancelledTaxes: ").append(toIndentedString(stakeCancelledTaxes)).append("\n");
    sb.append("    paidOutTaxes: ").append(toIndentedString(paidOutTaxes)).append("\n");
    sb.append("    jackpotContribution: ").append(toIndentedString(jackpotContribution)).append("\n");
    sb.append("    megaJackpotContribution: ").append(toIndentedString(megaJackpotContribution)).append("\n");
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
