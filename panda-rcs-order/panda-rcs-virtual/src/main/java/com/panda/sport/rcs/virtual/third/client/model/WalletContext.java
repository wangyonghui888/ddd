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
import com.panda.sport.rcs.virtual.third.client.model.Context;
import java.io.IOException;

/**
 * 3rd party seamless wallet configuration for realtime notifications on sessions and wallet management. 
 */
@ApiModel(description = "3rd party seamless wallet configuration for realtime notifications on sessions and wallet management. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class WalletContext extends Context {
  /**
   * Choose what version of the wallet feature you&#39;d like to use :\\n   v3. The latest version 
   */
  @JsonAdapter(VersionEnum.Adapter.class)
  public enum VersionEnum {
    DISABLED("Disabled"),
    
    V3("v3");

    private String value;

    VersionEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static VersionEnum fromValue(String text) {
      for (VersionEnum b : VersionEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<VersionEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final VersionEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public VersionEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return VersionEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("version")
  private VersionEnum version = null;

  @SerializedName("baseUrl")
  private String baseUrl = null;

  @SerializedName("onCredit")
  private Boolean onCredit = null;

  @SerializedName("signature")
  private String signature = null;

  @SerializedName("bulkSize")
  private Integer bulkSize = null;

  /**
   * Allows to enable/ disable configuration of solve notifications. 
   */
  @JsonAdapter(SolveNotificationsEnum.Adapter.class)
  public enum SolveNotificationsEnum {
    DISABLED("Disabled"),
    
    SEQUENTIAL("Sequential"),
    
    PARALLEL("Parallel");

    private String value;

    SolveNotificationsEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static SolveNotificationsEnum fromValue(String text) {
      for (SolveNotificationsEnum b : SolveNotificationsEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<SolveNotificationsEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final SolveNotificationsEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public SolveNotificationsEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return SolveNotificationsEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("solveNotifications")
  private SolveNotificationsEnum solveNotifications = null;

  @SerializedName("keepAlive")
  private Boolean keepAlive = null;

  @SerializedName("sellDetails")
  private Boolean sellDetails = null;

  @SerializedName("payoutDetails")
  private Boolean payoutDetails = null;

  @SerializedName("cancelDetails")
  private Boolean cancelDetails = null;

  @SerializedName("solveDetails")
  private Boolean solveDetails = null;

  public WalletContext version(VersionEnum version) {
    this.version = version;
    return this;
  }

   /**
   * Choose what version of the wallet feature you&#39;d like to use :\\n   v3. The latest version 
   * @return version
  **/
  @ApiModelProperty(value = "Choose what version of the wallet feature you'd like to use :\\n   v3. The latest version ")
  public VersionEnum getVersion() {
    return version;
  }

  public void setVersion(VersionEnum version) {
    this.version = version;
  }

  public WalletContext baseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return this;
  }

   /**
   * This is the URL that identifies your wallet feature. If you have doubts about how to use it, please contact Support.  
   * @return baseUrl
  **/
  @ApiModelProperty(value = "This is the URL that identifies your wallet feature. If you have doubts about how to use it, please contact Support.  ")
  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public WalletContext onCredit(Boolean onCredit) {
    this.onCredit = onCredit;
    return this;
  }

   /**
   * If this property is false, the wallet has infinity credit. The balance will be NULL. It works with no credit (infinity credit) if disabled. 
   * @return onCredit
  **/
  @ApiModelProperty(value = "If this property is false, the wallet has infinity credit. The balance will be NULL. It works with no credit (infinity credit) if disabled. ")
  public Boolean isOnCredit() {
    return onCredit;
  }

  public void setOnCredit(Boolean onCredit) {
    this.onCredit = onCredit;
  }

  public WalletContext signature(String signature) {
    this.signature = signature;
    return this;
  }

   /**
   * APi signature.
   * @return signature
  **/
  @ApiModelProperty(value = "APi signature.")
  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public WalletContext bulkSize(Integer bulkSize) {
    this.bulkSize = bulkSize;
    return this;
  }

   /**
   * Maximum number of items in bulk requests.
   * @return bulkSize
  **/
  @ApiModelProperty(value = "Maximum number of items in bulk requests.")
  public Integer getBulkSize() {
    return bulkSize;
  }

  public void setBulkSize(Integer bulkSize) {
    this.bulkSize = bulkSize;
  }

  public WalletContext solveNotifications(SolveNotificationsEnum solveNotifications) {
    this.solveNotifications = solveNotifications;
    return this;
  }

   /**
   * Allows to enable/ disable configuration of solve notifications. 
   * @return solveNotifications
  **/
  @ApiModelProperty(value = "Allows to enable/ disable configuration of solve notifications. ")
  public SolveNotificationsEnum getSolveNotifications() {
    return solveNotifications;
  }

  public void setSolveNotifications(SolveNotificationsEnum solveNotifications) {
    this.solveNotifications = solveNotifications;
  }

  public WalletContext keepAlive(Boolean keepAlive) {
    this.keepAlive = keepAlive;
    return this;
  }

   /**
   * Allows to enable/disable KeepAlive, notifications. 
   * @return keepAlive
  **/
  @ApiModelProperty(value = "Allows to enable/disable KeepAlive, notifications. ")
  public Boolean isKeepAlive() {
    return keepAlive;
  }

  public void setKeepAlive(Boolean keepAlive) {
    this.keepAlive = keepAlive;
  }

  public WalletContext sellDetails(Boolean sellDetails) {
    this.sellDetails = sellDetails;
    return this;
  }

   /**
   * Enable/disable details on selling 
   * @return sellDetails
  **/
  @ApiModelProperty(value = "Enable/disable details on selling ")
  public Boolean isSellDetails() {
    return sellDetails;
  }

  public void setSellDetails(Boolean sellDetails) {
    this.sellDetails = sellDetails;
  }

  public WalletContext payoutDetails(Boolean payoutDetails) {
    this.payoutDetails = payoutDetails;
    return this;
  }

   /**
   * Enable/disable details on payout notication 
   * @return payoutDetails
  **/
  @ApiModelProperty(value = "Enable/disable details on payout notication ")
  public Boolean isPayoutDetails() {
    return payoutDetails;
  }

  public void setPayoutDetails(Boolean payoutDetails) {
    this.payoutDetails = payoutDetails;
  }

  public WalletContext cancelDetails(Boolean cancelDetails) {
    this.cancelDetails = cancelDetails;
    return this;
  }

   /**
   * Enable/disable details on cancel notication 
   * @return cancelDetails
  **/
  @ApiModelProperty(value = "Enable/disable details on cancel notication ")
  public Boolean isCancelDetails() {
    return cancelDetails;
  }

  public void setCancelDetails(Boolean cancelDetails) {
    this.cancelDetails = cancelDetails;
  }

  public WalletContext solveDetails(Boolean solveDetails) {
    this.solveDetails = solveDetails;
    return this;
  }

   /**
   * Enable/disable details on solve notication 
   * @return solveDetails
  **/
  @ApiModelProperty(value = "Enable/disable details on solve notication ")
  public Boolean isSolveDetails() {
    return solveDetails;
  }

  public void setSolveDetails(Boolean solveDetails) {
    this.solveDetails = solveDetails;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WalletContext walletContext = (WalletContext) o;
    return Objects.equals(this.version, walletContext.version) &&
        Objects.equals(this.baseUrl, walletContext.baseUrl) &&
        Objects.equals(this.onCredit, walletContext.onCredit) &&
        Objects.equals(this.signature, walletContext.signature) &&
        Objects.equals(this.bulkSize, walletContext.bulkSize) &&
        Objects.equals(this.solveNotifications, walletContext.solveNotifications) &&
        Objects.equals(this.keepAlive, walletContext.keepAlive) &&
        Objects.equals(this.sellDetails, walletContext.sellDetails) &&
        Objects.equals(this.payoutDetails, walletContext.payoutDetails) &&
        Objects.equals(this.cancelDetails, walletContext.cancelDetails) &&
        Objects.equals(this.solveDetails, walletContext.solveDetails) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(version, baseUrl, onCredit, signature, bulkSize, solveNotifications, keepAlive, sellDetails, payoutDetails, cancelDetails, solveDetails, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WalletContext {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    baseUrl: ").append(toIndentedString(baseUrl)).append("\n");
    sb.append("    onCredit: ").append(toIndentedString(onCredit)).append("\n");
    sb.append("    signature: ").append(toIndentedString(signature)).append("\n");
    sb.append("    bulkSize: ").append(toIndentedString(bulkSize)).append("\n");
    sb.append("    solveNotifications: ").append(toIndentedString(solveNotifications)).append("\n");
    sb.append("    keepAlive: ").append(toIndentedString(keepAlive)).append("\n");
    sb.append("    sellDetails: ").append(toIndentedString(sellDetails)).append("\n");
    sb.append("    payoutDetails: ").append(toIndentedString(payoutDetails)).append("\n");
    sb.append("    cancelDetails: ").append(toIndentedString(cancelDetails)).append("\n");
    sb.append("    solveDetails: ").append(toIndentedString(solveDetails)).append("\n");
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
