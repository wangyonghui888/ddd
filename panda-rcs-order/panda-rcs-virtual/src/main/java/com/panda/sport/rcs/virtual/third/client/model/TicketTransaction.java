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
import com.panda.sport.rcs.virtual.third.client.model.ServerTicket;
import com.panda.sport.rcs.virtual.third.client.model.Transaction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Object with information about the ticket and the transaction result. 
 */
@ApiModel(description = "Object with information about the ticket and the transaction result. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class TicketTransaction {
  @SerializedName("ticket")
  private ServerTicket ticket = null;

  @SerializedName("transaction")
  private List<Transaction> transaction = null;

  public TicketTransaction ticket(ServerTicket ticket) {
    this.ticket = ticket;
    return this;
  }

   /**
   * Get ticket
   * @return ticket
  **/
  @ApiModelProperty(value = "")
  public ServerTicket getTicket() {
    return ticket;
  }

  public void setTicket(ServerTicket ticket) {
    this.ticket = ticket;
  }

  public TicketTransaction transaction(List<Transaction> transaction) {
    this.transaction = transaction;
    return this;
  }

  public TicketTransaction addTransactionItem(Transaction transactionItem) {
    if (this.transaction == null) {
      this.transaction = new ArrayList<Transaction>();
    }
    this.transaction.add(transactionItem);
    return this;
  }

   /**
   * Get transaction
   * @return transaction
  **/
  @ApiModelProperty(value = "")
  public List<Transaction> getTransaction() {
    return transaction;
  }

  public void setTransaction(List<Transaction> transaction) {
    this.transaction = transaction;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TicketTransaction ticketTransaction = (TicketTransaction) o;
    return Objects.equals(this.ticket, ticketTransaction.ticket) &&
        Objects.equals(this.transaction, ticketTransaction.transaction);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ticket, transaction);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TicketTransaction {\n");
    
    sb.append("    ticket: ").append(toIndentedString(ticket)).append("\n");
    sb.append("    transaction: ").append(toIndentedString(transaction)).append("\n");
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

