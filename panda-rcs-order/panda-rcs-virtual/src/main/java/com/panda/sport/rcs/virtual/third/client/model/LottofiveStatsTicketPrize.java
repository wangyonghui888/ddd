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
 * Information about a prize of a ticket. 
 */
@ApiModel(description = "Information about a prize of a ticket. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class LottofiveStatsTicketPrize {
  @SerializedName("ticketId")
  private Integer ticketId = null;

  @SerializedName("prize")
  private Double prize = null;

  @SerializedName("city")
  private String city = null;

  public LottofiveStatsTicketPrize ticketId(Integer ticketId) {
    this.ticketId = ticketId;
    return this;
  }

   /**
   * The id of the ticket
   * @return ticketId
  **/
  @ApiModelProperty(value = "The id of the ticket")
  public Integer getTicketId() {
    return ticketId;
  }

  public void setTicketId(Integer ticketId) {
    this.ticketId = ticketId;
  }

  public LottofiveStatsTicketPrize prize(Double prize) {
    this.prize = prize;
    return this;
  }

   /**
   * The prize of the ticket
   * @return prize
  **/
  @ApiModelProperty(value = "The prize of the ticket")
  public Double getPrize() {
    return prize;
  }

  public void setPrize(Double prize) {
    this.prize = prize;
  }

  public LottofiveStatsTicketPrize city(String city) {
    this.city = city;
    return this;
  }

   /**
   * The city of the ticket
   * @return city
  **/
  @ApiModelProperty(value = "The city of the ticket")
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LottofiveStatsTicketPrize lottofiveStatsTicketPrize = (LottofiveStatsTicketPrize) o;
    return Objects.equals(this.ticketId, lottofiveStatsTicketPrize.ticketId) &&
        Objects.equals(this.prize, lottofiveStatsTicketPrize.prize) &&
        Objects.equals(this.city, lottofiveStatsTicketPrize.city);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ticketId, prize, city);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LottofiveStatsTicketPrize {\n");
    
    sb.append("    ticketId: ").append(toIndentedString(ticketId)).append("\n");
    sb.append("    prize: ").append(toIndentedString(prize)).append("\n");
    sb.append("    city: ").append(toIndentedString(city)).append("\n");
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

