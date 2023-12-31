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
import com.panda.sport.rcs.virtual.third.client.model.BaseWalletSellResponse;
import com.panda.sport.rcs.virtual.third.client.model.WalletTransaction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MultiWalletSellResponse
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class MultiWalletSellResponse extends BaseWalletSellResponse {
  @SerializedName("transactions")
  private List<WalletTransaction> transactions = null;

  public MultiWalletSellResponse transactions(List<WalletTransaction> transactions) {
    this.transactions = transactions;
    return this;
  }

  public MultiWalletSellResponse addTransactionsItem(WalletTransaction transactionsItem) {
    if (this.transactions == null) {
      this.transactions = new ArrayList<WalletTransaction>();
    }
    this.transactions.add(transactionsItem);
    return this;
  }

   /**
   * Array of all transactions resulted from given operation
   * @return transactions
  **/
  @ApiModelProperty(value = "Array of all transactions resulted from given operation")
  public List<WalletTransaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<WalletTransaction> transactions) {
    this.transactions = transactions;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MultiWalletSellResponse multiWalletSellResponse = (MultiWalletSellResponse) o;
    return Objects.equals(this.transactions, multiWalletSellResponse.transactions) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transactions, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MultiWalletSellResponse {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    transactions: ").append(toIndentedString(transactions)).append("\n");
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

