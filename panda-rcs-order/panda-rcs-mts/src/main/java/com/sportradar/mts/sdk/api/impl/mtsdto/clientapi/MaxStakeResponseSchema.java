package com.sportradar.mts.sdk.api.impl.mtsdto.clientapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"ticketId", "maxStake", "timestampUtc"})
public class MaxStakeResponseSchema
  implements Serializable
{
  @JsonProperty("ticketId")
  @JsonPropertyDescription("Ticket ID (from the original response)")
  private String ticketId;
  @JsonProperty("maxStake")
  @JsonPropertyDescription("Maximum reoffer stake (quantity multiplied by 10000 and rounded to a long value)")
  private Long maxStake;
  @JsonProperty("timestampUtc")
  private Long timestampUtc;
  
  @JsonProperty("code")
  private String code;
  
  private static final long serialVersionUID = 6179117293752634072L;
  
  public MaxStakeResponseSchema() {}
  
  public MaxStakeResponseSchema(String ticketId, Long maxStake, Long timestampUtc) {
    this.ticketId = ticketId;
    this.maxStake = maxStake;
    this.timestampUtc = timestampUtc;
  }

  @JsonProperty("ticketId")
  public String getTicketId() { return this.ticketId; }
  
  @JsonProperty("ticketId")
  public void setTicketId(String ticketId) { this.ticketId = ticketId; }

  @JsonProperty("maxStake")
  public Long getMaxStake() { return this.maxStake; }

  @JsonProperty("maxStake")
  public void setMaxStake(Long maxStake) { this.maxStake = maxStake; }

  @JsonProperty("code")
  public String getCode() { return this.code; }
  
  @JsonProperty("code")
  public void setCode(String code) { this.code = code; }

  
  @JsonProperty("timestampUtc")
  public Long getTimestampUtc() { return this.timestampUtc; }

  @JsonProperty("timestampUtc")
  public void setTimestampUtc(Long timestampUtc) { this.timestampUtc = timestampUtc; }



  
  public String toString() { return ToStringBuilder.reflectionToString(this); }



  
  public int hashCode() { return (new HashCodeBuilder()).append(this.ticketId).append(this.maxStake).append(this.timestampUtc).toHashCode(); }


  
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof MaxStakeResponseSchema)) {
      return false;
    }
    MaxStakeResponseSchema rhs = (MaxStakeResponseSchema)other;
    return (new EqualsBuilder()).append(this.ticketId, rhs.ticketId).append(this.maxStake, rhs.maxStake).append(this.timestampUtc, rhs.timestampUtc).isEquals();
  }
}
