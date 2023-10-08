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
 * Information about wallet properties and status. This wallet could be a seamless wallet or a Promotional wallet. 
 */
@ApiModel(description = "Information about wallet properties and status. This wallet could be a seamless wallet or a Promotional wallet. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class Wallet {
  @SerializedName("id")
  private Long id = null;

  @SerializedName("extId")
  private String extId = null;

  @SerializedName("entityId")
  private Integer entityId = null;

  @SerializedName("priority")
  private Integer priority = null;

  @SerializedName("currency")
  private String currency = null;

  @SerializedName("description")
  private String description = null;

  @SerializedName("startDate")
  private OffsetDateTime startDate = null;

  @SerializedName("endDate")
  private OffsetDateTime endDate = null;

  @SerializedName("lastModification")
  private OffsetDateTime lastModification = null;

  @SerializedName("extData")
  private String extData = null;

  /**
   * The wallet status
   */
  @JsonAdapter(StatusEnum.Adapter.class)
  public enum StatusEnum {
    ENABLED("Enabled"),
    
    DISABLED("Disabled"),
    
    DELETED("Deleted");

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

  @SerializedName("isPromotion")
  private Boolean isPromotion = null;

  @SerializedName("balance")
  private Double balance = null;

  public Wallet id(Long id) {
    this.id = id;
    return this;
  }

   /**
   * Unique wallet identifier.
   * @return id
  **/
  @ApiModelProperty(value = "Unique wallet identifier.")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Wallet extId(String extId) {
    this.extId = extId;
    return this;
  }

   /**
   * Third party wallet identifier.
   * @return extId
  **/
  @ApiModelProperty(value = "Third party wallet identifier.")
  public String getExtId() {
    return extId;
  }

  public void setExtId(String extId) {
    this.extId = extId;
  }

  public Wallet entityId(Integer entityId) {
    this.entityId = entityId;
    return this;
  }

   /**
   * Goldenrace entity/user identifier. Is the owner of this wallet.
   * @return entityId
  **/
  @ApiModelProperty(value = "Goldenrace entity/user identifier. Is the owner of this wallet.")
  public Integer getEntityId() {
    return entityId;
  }

  public void setEntityId(Integer entityId) {
    this.entityId = entityId;
  }

  public Wallet priority(Integer priority) {
    this.priority = priority;
    return this;
  }

   /**
   * Signed integer, with priority of this wallet to be used when more than one wallet is available. Positive values. With 0 being the lowest priority. If several wallets have the same priority, the wallet with the lowest Id value will be the highest priority. 
   * @return priority
  **/
  @ApiModelProperty(value = "Signed integer, with priority of this wallet to be used when more than one wallet is available. Positive values. With 0 being the lowest priority. If several wallets have the same priority, the wallet with the lowest Id value will be the highest priority. ")
  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public Wallet currency(String currency) {
    this.currency = currency;
    return this;
  }

   /**
   * The currency code of the wallet.
   * @return currency
  **/
  @ApiModelProperty(value = "The currency code of the wallet.")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public Wallet description(String description) {
    this.description = description;
    return this;
  }

   /**
   * The description of the wallet.
   * @return description
  **/
  @ApiModelProperty(value = "The description of the wallet.")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Wallet startDate(OffsetDateTime startDate) {
    this.startDate = startDate;
    return this;
  }

   /**
   * the date from which this wallet is operational.
   * @return startDate
  **/
  @ApiModelProperty(value = "the date from which this wallet is operational.")
  public OffsetDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(OffsetDateTime startDate) {
    this.startDate = startDate;
  }

  public Wallet endDate(OffsetDateTime endDate) {
    this.endDate = endDate;
    return this;
  }

   /**
   * the date until this wallet is operational.
   * @return endDate
  **/
  @ApiModelProperty(value = "the date until this wallet is operational.")
  public OffsetDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(OffsetDateTime endDate) {
    this.endDate = endDate;
  }

  public Wallet lastModification(OffsetDateTime lastModification) {
    this.lastModification = lastModification;
    return this;
  }

   /**
   * the last modification wallet date.
   * @return lastModification
  **/
  @ApiModelProperty(value = "the last modification wallet date.")
  public OffsetDateTime getLastModification() {
    return lastModification;
  }

  public void setLastModification(OffsetDateTime lastModification) {
    this.lastModification = lastModification;
  }

  public Wallet extData(String extData) {
    this.extData = extData;
    return this;
  }

   /**
   * Third party JSON type information with for example the initial balance.
   * @return extData
  **/
  @ApiModelProperty(value = "Third party JSON type information with for example the initial balance.")
  public String getExtData() {
    return extData;
  }

  public void setExtData(String extData) {
    this.extData = extData;
  }

  public Wallet status(StatusEnum status) {
    this.status = status;
    return this;
  }

   /**
   * The wallet status
   * @return status
  **/
  @ApiModelProperty(value = "The wallet status")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public Wallet isPromotion(Boolean isPromotion) {
    this.isPromotion = isPromotion;
    return this;
  }

   /**
   * The wallet is a temporaly wallet for a promotion. If this option is enabled, and the user win, the money used in the ticket will be discount in the prize.
   * @return isPromotion
  **/
  @ApiModelProperty(value = "The wallet is a temporaly wallet for a promotion. If this option is enabled, and the user win, the money used in the ticket will be discount in the prize.")
  public Boolean isIsPromotion() {
    return isPromotion;
  }

  public void setIsPromotion(Boolean isPromotion) {
    this.isPromotion = isPromotion;
  }

  public Wallet balance(Double balance) {
    this.balance = balance;
    return this;
  }

   /**
   * The actual balance of the wallet.
   * @return balance
  **/
  @ApiModelProperty(value = "The actual balance of the wallet.")
  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Wallet wallet = (Wallet) o;
    return Objects.equals(this.id, wallet.id) &&
        Objects.equals(this.extId, wallet.extId) &&
        Objects.equals(this.entityId, wallet.entityId) &&
        Objects.equals(this.priority, wallet.priority) &&
        Objects.equals(this.currency, wallet.currency) &&
        Objects.equals(this.description, wallet.description) &&
        Objects.equals(this.startDate, wallet.startDate) &&
        Objects.equals(this.endDate, wallet.endDate) &&
        Objects.equals(this.lastModification, wallet.lastModification) &&
        Objects.equals(this.extData, wallet.extData) &&
        Objects.equals(this.status, wallet.status) &&
        Objects.equals(this.isPromotion, wallet.isPromotion) &&
        Objects.equals(this.balance, wallet.balance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, extId, entityId, priority, currency, description, startDate, endDate, lastModification, extData, status, isPromotion, balance);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Wallet {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    extId: ").append(toIndentedString(extId)).append("\n");
    sb.append("    entityId: ").append(toIndentedString(entityId)).append("\n");
    sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    lastModification: ").append(toIndentedString(lastModification)).append("\n");
    sb.append("    extData: ").append(toIndentedString(extData)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    isPromotion: ").append(toIndentedString(isPromotion)).append("\n");
    sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
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
