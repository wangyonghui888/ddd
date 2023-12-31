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
 * Information about credit transaction operations. Includes credit before and after the operation for continous credit checking.  Linked with following major entities  | Field        | Description                                                                                               | |--            | --                                                                                                        | | entityId     | Entity user owner of a wallet to this transaction.                                                        | | currency     | Curency code, linked to wallet.                                                                           | | walletId     | Wallet id, owner of credit related to this transaction.                                                   | | operatorId   | Entity operator, can be manager/client/api, wich can perform operations on current wallet. Can be null.   | | operatorAPI  | Entity operator session, can be manager/client/external. Can be null for automatic server operations.     | | ticketId     | TicketId when transaction is related to a ticket.                                                         |  Addditional fields | Field        | Description                                                                                   | |--            | --                                                                                            | | method       | Path used on API to perform this operation | | params       | JSON Object with information related to transaction, so human readable description can be generated in different languages. | 
 */
@ApiModel(description = "Information about credit transaction operations. Includes credit before and after the operation for continous credit checking.  Linked with following major entities  | Field        | Description                                                                                               | |--            | --                                                                                                        | | entityId     | Entity user owner of a wallet to this transaction.                                                        | | currency     | Curency code, linked to wallet.                                                                           | | walletId     | Wallet id, owner of credit related to this transaction.                                                   | | operatorId   | Entity operator, can be manager/client/api, wich can perform operations on current wallet. Can be null.   | | operatorAPI  | Entity operator session, can be manager/client/external. Can be null for automatic server operations.     | | ticketId     | TicketId when transaction is related to a ticket.                                                         |  Addditional fields | Field        | Description                                                                                   | |--            | --                                                                                            | | method       | Path used on API to perform this operation | | params       | JSON Object with information related to transaction, so human readable description can be generated in different languages. | ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class Transaction {
  @SerializedName("transactionId")
  private Long transactionId = null;

  @SerializedName("extTransactionId")
  private String extTransactionId = null;

  @SerializedName("extTransactionData")
  private String extTransactionData = null;

  @SerializedName("entityId")
  private Integer entityId = null;

  @SerializedName("walletId")
  private Long walletId = null;

  @SerializedName("date")
  private OffsetDateTime date = null;

  @SerializedName("previousCredit")
  private Double previousCredit = null;

  @SerializedName("newCredit")
  private Double newCredit = null;

  @SerializedName("changeCredit")
  private Double changeCredit = null;

  @SerializedName("currency")
  private String currency = null;

  @SerializedName("ticketId")
  private Long ticketId = null;

  @SerializedName("operatorId")
  private Integer operatorId = null;

  /**
   * Entity operator, can be manager/client/api, wich can perform operations on current wallet. Can be null if the operator is the system.
   */
  @JsonAdapter(OperatorAPIEnum.Adapter.class)
  public enum OperatorAPIEnum {
    MANAGER("MANAGER"),
    
    CLIENT("CLIENT"),
    
    EXTERNAL("EXTERNAL"),
    
    SYSTEM("SYSTEM");

    private String value;

    OperatorAPIEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static OperatorAPIEnum fromValue(String text) {
      for (OperatorAPIEnum b : OperatorAPIEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<OperatorAPIEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final OperatorAPIEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public OperatorAPIEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return OperatorAPIEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("operatorAPI")
  private OperatorAPIEnum operatorAPI = null;

  @SerializedName("description")
  private String description = null;

  @SerializedName("method")
  private String method = null;

  @SerializedName("params")
  private String params = null;

  public Transaction transactionId(Long transactionId) {
    this.transactionId = transactionId;
    return this;
  }

   /**
   * Transaction Id , autoincremental integer for unique identification of a transaction.
   * @return transactionId
  **/
  @ApiModelProperty(value = "Transaction Id , autoincremental integer for unique identification of a transaction.")
  public Long getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(Long transactionId) {
    this.transactionId = transactionId;
  }

  public Transaction extTransactionId(String extTransactionId) {
    this.extTransactionId = extTransactionId;
    return this;
  }

   /**
   * External transaction Id, should be unique for same walletId. It&#39;s the third party transaction identifier
   * @return extTransactionId
  **/
  @ApiModelProperty(value = "External transaction Id, should be unique for same walletId. It's the third party transaction identifier")
  public String getExtTransactionId() {
    return extTransactionId;
  }

  public void setExtTransactionId(String extTransactionId) {
    this.extTransactionId = extTransactionId;
  }

  public Transaction extTransactionData(String extTransactionData) {
    this.extTransactionData = extTransactionData;
    return this;
  }

   /**
   * Third party external transaction JSON represented data.
   * @return extTransactionData
  **/
  @ApiModelProperty(value = "Third party external transaction JSON represented data.")
  public String getExtTransactionData() {
    return extTransactionData;
  }

  public void setExtTransactionData(String extTransactionData) {
    this.extTransactionData = extTransactionData;
  }

  public Transaction entityId(Integer entityId) {
    this.entityId = entityId;
    return this;
  }

   /**
   * Entity user owner of a wallet to this transaction.
   * @return entityId
  **/
  @ApiModelProperty(value = "Entity user owner of a wallet to this transaction.")
  public Integer getEntityId() {
    return entityId;
  }

  public void setEntityId(Integer entityId) {
    this.entityId = entityId;
  }

  public Transaction walletId(Long walletId) {
    this.walletId = walletId;
    return this;
  }

   /**
   * The wallet id of operation.
   * @return walletId
  **/
  @ApiModelProperty(value = "The wallet id of operation.")
  public Long getWalletId() {
    return walletId;
  }

  public void setWalletId(Long walletId) {
    this.walletId = walletId;
  }

  public Transaction date(OffsetDateTime date) {
    this.date = date;
    return this;
  }

   /**
   * Date of transaction
   * @return date
  **/
  @ApiModelProperty(value = "Date of transaction")
  public OffsetDateTime getDate() {
    return date;
  }

  public void setDate(OffsetDateTime date) {
    this.date = date;
  }

  public Transaction previousCredit(Double previousCredit) {
    this.previousCredit = previousCredit;
    return this;
  }

   /**
   * Previous Credit (Before Transaction)
   * @return previousCredit
  **/
  @ApiModelProperty(value = "Previous Credit (Before Transaction)")
  public Double getPreviousCredit() {
    return previousCredit;
  }

  public void setPreviousCredit(Double previousCredit) {
    this.previousCredit = previousCredit;
  }

  public Transaction newCredit(Double newCredit) {
    this.newCredit = newCredit;
    return this;
  }

   /**
   * Resulting Credit (After Transaction)
   * @return newCredit
  **/
  @ApiModelProperty(value = "Resulting Credit (After Transaction)")
  public Double getNewCredit() {
    return newCredit;
  }

  public void setNewCredit(Double newCredit) {
    this.newCredit = newCredit;
  }

  public Transaction changeCredit(Double changeCredit) {
    this.changeCredit = changeCredit;
    return this;
  }

   /**
   * Amount of credit change requested on transaction.  In case that a wallet system is linked with a seamless wallet without transactional check,  the amounts on newCredit and previousCredit, are based on an incremental estimation,  and could not match 100% to changeCredit amount on the scenario of concurrent credit modifications. 
   * @return changeCredit
  **/
  @ApiModelProperty(value = "Amount of credit change requested on transaction.  In case that a wallet system is linked with a seamless wallet without transactional check,  the amounts on newCredit and previousCredit, are based on an incremental estimation,  and could not match 100% to changeCredit amount on the scenario of concurrent credit modifications. ")
  public Double getChangeCredit() {
    return changeCredit;
  }

  public void setChangeCredit(Double changeCredit) {
    this.changeCredit = changeCredit;
  }

  public Transaction currency(String currency) {
    this.currency = currency;
    return this;
  }

   /**
   * ISO Currency code
   * @return currency
  **/
  @ApiModelProperty(value = "ISO Currency code")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public Transaction ticketId(Long ticketId) {
    this.ticketId = ticketId;
    return this;
  }

   /**
   * Entity Id , wich credit is modified
   * @return ticketId
  **/
  @ApiModelProperty(value = "Entity Id , wich credit is modified")
  public Long getTicketId() {
    return ticketId;
  }

  public void setTicketId(Long ticketId) {
    this.ticketId = ticketId;
  }

  public Transaction operatorId(Integer operatorId) {
    this.operatorId = operatorId;
    return this;
  }

   /**
   * Entity operator identifier. Can be null if operator is the system.
   * @return operatorId
  **/
  @ApiModelProperty(value = "Entity operator identifier. Can be null if operator is the system.")
  public Integer getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Integer operatorId) {
    this.operatorId = operatorId;
  }

  public Transaction operatorAPI(OperatorAPIEnum operatorAPI) {
    this.operatorAPI = operatorAPI;
    return this;
  }

   /**
   * Entity operator, can be manager/client/api, wich can perform operations on current wallet. Can be null if the operator is the system.
   * @return operatorAPI
  **/
  @ApiModelProperty(value = "Entity operator, can be manager/client/api, wich can perform operations on current wallet. Can be null if the operator is the system.")
  public OperatorAPIEnum getOperatorAPI() {
    return operatorAPI;
  }

  public void setOperatorAPI(OperatorAPIEnum operatorAPI) {
    this.operatorAPI = operatorAPI;
  }

  public Transaction description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Human description of Transaction.
   * @return description
  **/
  @ApiModelProperty(value = "Human description of Transaction.")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Transaction method(String method) {
    this.method = method;
    return this;
  }

   /**
   * Path used on API to perform this operation
   * @return method
  **/
  @ApiModelProperty(value = "Path used on API to perform this operation")
  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public Transaction params(String params) {
    this.params = params;
    return this;
  }

   /**
   * JSON Object with information related to transaction, so human readable description can be generated in different languages.
   * @return params
  **/
  @ApiModelProperty(value = "JSON Object with information related to transaction, so human readable description can be generated in different languages.")
  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Transaction transaction = (Transaction) o;
    return Objects.equals(this.transactionId, transaction.transactionId) &&
        Objects.equals(this.extTransactionId, transaction.extTransactionId) &&
        Objects.equals(this.extTransactionData, transaction.extTransactionData) &&
        Objects.equals(this.entityId, transaction.entityId) &&
        Objects.equals(this.walletId, transaction.walletId) &&
        Objects.equals(this.date, transaction.date) &&
        Objects.equals(this.previousCredit, transaction.previousCredit) &&
        Objects.equals(this.newCredit, transaction.newCredit) &&
        Objects.equals(this.changeCredit, transaction.changeCredit) &&
        Objects.equals(this.currency, transaction.currency) &&
        Objects.equals(this.ticketId, transaction.ticketId) &&
        Objects.equals(this.operatorId, transaction.operatorId) &&
        Objects.equals(this.operatorAPI, transaction.operatorAPI) &&
        Objects.equals(this.description, transaction.description) &&
        Objects.equals(this.method, transaction.method) &&
        Objects.equals(this.params, transaction.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transactionId, extTransactionId, extTransactionData, entityId, walletId, date, previousCredit, newCredit, changeCredit, currency, ticketId, operatorId, operatorAPI, description, method, params);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Transaction {\n");
    
    sb.append("    transactionId: ").append(toIndentedString(transactionId)).append("\n");
    sb.append("    extTransactionId: ").append(toIndentedString(extTransactionId)).append("\n");
    sb.append("    extTransactionData: ").append(toIndentedString(extTransactionData)).append("\n");
    sb.append("    entityId: ").append(toIndentedString(entityId)).append("\n");
    sb.append("    walletId: ").append(toIndentedString(walletId)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    previousCredit: ").append(toIndentedString(previousCredit)).append("\n");
    sb.append("    newCredit: ").append(toIndentedString(newCredit)).append("\n");
    sb.append("    changeCredit: ").append(toIndentedString(changeCredit)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    ticketId: ").append(toIndentedString(ticketId)).append("\n");
    sb.append("    operatorId: ").append(toIndentedString(operatorId)).append("\n");
    sb.append("    operatorAPI: ").append(toIndentedString(operatorAPI)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    method: ").append(toIndentedString(method)).append("\n");
    sb.append("    params: ").append(toIndentedString(params)).append("\n");
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

