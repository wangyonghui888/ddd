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
 * Object with the result of transaction operations (CREATE, PAIDOUT or CANCEL) and describe the old credit, the new credit, the date of operation and the currency of the transaction. 
 */
@ApiModel(description = "Object with the result of transaction operations (CREATE, PAIDOUT or CANCEL) and describe the old credit, the new credit, the date of operation and the currency of the transaction. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class TransactionResult {
  /**
   * transaction types can be     - CREATE   - PAIDOUT   - CANCEL 
   */
  @JsonAdapter(TransTypeEnum.Adapter.class)
  public enum TransTypeEnum {
    CREATE("CREATE"),
    
    PAIDOUT("PAIDOUT"),
    
    CANCEL("CANCEL");

    private String value;

    TransTypeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static TransTypeEnum fromValue(String text) {
      for (TransTypeEnum b : TransTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<TransTypeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final TransTypeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public TransTypeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return TransTypeEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("transType")
  private TransTypeEnum transType = null;

  @SerializedName("oldCredit")
  private Double oldCredit = null;

  @SerializedName("newCredit")
  private Double newCredit = null;

  @SerializedName("currencyCode")
  private String currencyCode = null;

  @SerializedName("extTransactionId")
  private String extTransactionId = null;

  @SerializedName("date")
  private OffsetDateTime date = null;

  public TransactionResult transType(TransTypeEnum transType) {
    this.transType = transType;
    return this;
  }

   /**
   * transaction types can be     - CREATE   - PAIDOUT   - CANCEL 
   * @return transType
  **/
  @ApiModelProperty(value = "transaction types can be     - CREATE   - PAIDOUT   - CANCEL ")
  public TransTypeEnum getTransType() {
    return transType;
  }

  public void setTransType(TransTypeEnum transType) {
    this.transType = transType;
  }

  public TransactionResult oldCredit(Double oldCredit) {
    this.oldCredit = oldCredit;
    return this;
  }

   /**
   * Get oldCredit
   * @return oldCredit
  **/
  @ApiModelProperty(value = "")
  public Double getOldCredit() {
    return oldCredit;
  }

  public void setOldCredit(Double oldCredit) {
    this.oldCredit = oldCredit;
  }

  public TransactionResult newCredit(Double newCredit) {
    this.newCredit = newCredit;
    return this;
  }

   /**
   * Get newCredit
   * @return newCredit
  **/
  @ApiModelProperty(value = "")
  public Double getNewCredit() {
    return newCredit;
  }

  public void setNewCredit(Double newCredit) {
    this.newCredit = newCredit;
  }

  public TransactionResult currencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
    return this;
  }

   /**
   * ISO Currency code 
   * @return currencyCode
  **/
  @ApiModelProperty(value = "ISO Currency code ")
  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public TransactionResult extTransactionId(String extTransactionId) {
    this.extTransactionId = extTransactionId;
    return this;
  }

   /**
   * External Transaction Id 
   * @return extTransactionId
  **/
  @ApiModelProperty(value = "External Transaction Id ")
  public String getExtTransactionId() {
    return extTransactionId;
  }

  public void setExtTransactionId(String extTransactionId) {
    this.extTransactionId = extTransactionId;
  }

  public TransactionResult date(OffsetDateTime date) {
    this.date = date;
    return this;
  }

   /**
   * Date of transaction 
   * @return date
  **/
  @ApiModelProperty(value = "Date of transaction ")
  public OffsetDateTime getDate() {
    return date;
  }

  public void setDate(OffsetDateTime date) {
    this.date = date;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransactionResult transactionResult = (TransactionResult) o;
    return Objects.equals(this.transType, transactionResult.transType) &&
        Objects.equals(this.oldCredit, transactionResult.oldCredit) &&
        Objects.equals(this.newCredit, transactionResult.newCredit) &&
        Objects.equals(this.currencyCode, transactionResult.currencyCode) &&
        Objects.equals(this.extTransactionId, transactionResult.extTransactionId) &&
        Objects.equals(this.date, transactionResult.date);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transType, oldCredit, newCredit, currencyCode, extTransactionId, date);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransactionResult {\n");
    
    sb.append("    transType: ").append(toIndentedString(transType)).append("\n");
    sb.append("    oldCredit: ").append(toIndentedString(oldCredit)).append("\n");
    sb.append("    newCredit: ").append(toIndentedString(newCredit)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    extTransactionId: ").append(toIndentedString(extTransactionId)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
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

