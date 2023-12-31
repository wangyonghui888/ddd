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
import com.panda.sport.rcs.virtual.third.client.model.ScaledPayoutTax;
import com.panda.sport.rcs.virtual.third.client.model.StakeTax;
import com.panda.sport.rcs.virtual.third.client.model.TaxPolicy;
import java.io.IOException;

/**
 * Balkans tax policy: Calculation Mode -&gt; PROGRESSIVE Rounding Mode -&gt; HALF-UP to Currency decimals Exclude Stake -&gt; TRUE Include Jackpot -&gt; FALSE 
 */
@ApiModel(description = "Balkans tax policy: Calculation Mode -> PROGRESSIVE Rounding Mode -> HALF-UP to Currency decimals Exclude Stake -> TRUE Include Jackpot -> FALSE ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class BalkansTaxPolicy extends TaxPolicy {
  @SerializedName("stakeTax")
  private StakeTax stakeTax = null;

  @SerializedName("payoutTax")
  private ScaledPayoutTax payoutTax = null;

  public BalkansTaxPolicy stakeTax(StakeTax stakeTax) {
    this.stakeTax = stakeTax;
    return this;
  }

   /**
   * Get stakeTax
   * @return stakeTax
  **/
  @ApiModelProperty(value = "")
  public StakeTax getStakeTax() {
    return stakeTax;
  }

  public void setStakeTax(StakeTax stakeTax) {
    this.stakeTax = stakeTax;
  }

  public BalkansTaxPolicy payoutTax(ScaledPayoutTax payoutTax) {
    this.payoutTax = payoutTax;
    return this;
  }

   /**
   * Get payoutTax
   * @return payoutTax
  **/
  @ApiModelProperty(value = "")
  public ScaledPayoutTax getPayoutTax() {
    return payoutTax;
  }

  public void setPayoutTax(ScaledPayoutTax payoutTax) {
    this.payoutTax = payoutTax;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BalkansTaxPolicy balkansTaxPolicy = (BalkansTaxPolicy) o;
    return Objects.equals(this.stakeTax, balkansTaxPolicy.stakeTax) &&
        Objects.equals(this.payoutTax, balkansTaxPolicy.payoutTax) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stakeTax, payoutTax, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BalkansTaxPolicy {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    stakeTax: ").append(toIndentedString(stakeTax)).append("\n");
    sb.append("    payoutTax: ").append(toIndentedString(payoutTax)).append("\n");
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

