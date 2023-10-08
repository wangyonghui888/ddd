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
 * Object to define bank data information 
 */
@ApiModel(description = "Object to define bank data information ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class CompanyBankInfo {
  @SerializedName("name")
  private String name = null;

  @SerializedName("accountNumber")
  private String accountNumber = null;

  @SerializedName("identifierCode")
  private String identifierCode = null;

  @SerializedName("ibanNumber")
  private String ibanNumber = null;

  public CompanyBankInfo name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Bank name. 
   * @return name
  **/
  @ApiModelProperty(value = "Bank name. ")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CompanyBankInfo accountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
    return this;
  }

   /**
   * Bank Account Number (a/c no). 
   * @return accountNumber
  **/
  @ApiModelProperty(value = "Bank Account Number (a/c no). ")
  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public CompanyBankInfo identifierCode(String identifierCode) {
    this.identifierCode = identifierCode;
    return this;
  }

   /**
   * Bank identifier code (BIC). 
   * @return identifierCode
  **/
  @ApiModelProperty(value = "Bank identifier code (BIC). ")
  public String getIdentifierCode() {
    return identifierCode;
  }

  public void setIdentifierCode(String identifierCode) {
    this.identifierCode = identifierCode;
  }

  public CompanyBankInfo ibanNumber(String ibanNumber) {
    this.ibanNumber = ibanNumber;
    return this;
  }

   /**
   * IBAN Number. 
   * @return ibanNumber
  **/
  @ApiModelProperty(value = "IBAN Number. ")
  public String getIbanNumber() {
    return ibanNumber;
  }

  public void setIbanNumber(String ibanNumber) {
    this.ibanNumber = ibanNumber;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompanyBankInfo companyBankInfo = (CompanyBankInfo) o;
    return Objects.equals(this.name, companyBankInfo.name) &&
        Objects.equals(this.accountNumber, companyBankInfo.accountNumber) &&
        Objects.equals(this.identifierCode, companyBankInfo.identifierCode) &&
        Objects.equals(this.ibanNumber, companyBankInfo.ibanNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, accountNumber, identifierCode, ibanNumber);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompanyBankInfo {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    accountNumber: ").append(toIndentedString(accountNumber)).append("\n");
    sb.append("    identifierCode: ").append(toIndentedString(identifierCode)).append("\n");
    sb.append("    ibanNumber: ").append(toIndentedString(ibanNumber)).append("\n");
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
