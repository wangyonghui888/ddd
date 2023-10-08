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
import org.threeten.bp.LocalDate;

/**
 * Entity contact details. Non inherited properties, used at entity level to provide detailed contact information of all entitie roles associated. 
 */
@ApiModel(description = "Entity contact details. Non inherited properties, used at entity level to provide detailed contact information of all entitie roles associated. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class ContactContext extends Context {
  @SerializedName("firstname")
  private String firstname = null;

  @SerializedName("lastname")
  private String lastname = null;

  /**
   * Gender of the contact person for this entity. 
   */
  @JsonAdapter(GenderEnum.Adapter.class)
  public enum GenderEnum {
    FEMALE("Female"),
    
    MALE("Male"),
    
    UNDISCLOSED("Undisclosed");

    private String value;

    GenderEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static GenderEnum fromValue(String text) {
      for (GenderEnum b : GenderEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<GenderEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final GenderEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public GenderEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return GenderEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("gender")
  private GenderEnum gender = null;

  @SerializedName("addressLine1")
  private String addressLine1 = null;

  @SerializedName("addressLine2")
  private String addressLine2 = null;

  @SerializedName("city")
  private String city = null;

  @SerializedName("zipcode")
  private String zipcode = null;

  @SerializedName("country")
  private String country = null;

  @SerializedName("companyGroupName")
  private String companyGroupName = null;

  @SerializedName("dateOfBirth")
  private LocalDate dateOfBirth = null;

  @SerializedName("email")
  private String email = null;

  @SerializedName("phone")
  private String phone = null;

  @SerializedName("phoneOther")
  private String phoneOther = null;

  @SerializedName("fax")
  private String fax = null;

  @SerializedName("icqNumber")
  private String icqNumber = null;

  @SerializedName("skypeName")
  private String skypeName = null;

  @SerializedName("vat")
  private String vat = null;

  @SerializedName("website")
  private String website = null;

  public ContactContext firstname(String firstname) {
    this.firstname = firstname;
    return this;
  }

   /**
   * First name of the contact person for this entity. 
   * @return firstname
  **/
  @ApiModelProperty(value = "First name of the contact person for this entity. ")
  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public ContactContext lastname(String lastname) {
    this.lastname = lastname;
    return this;
  }

   /**
   * Last name of the contact person for this entity. 
   * @return lastname
  **/
  @ApiModelProperty(value = "Last name of the contact person for this entity. ")
  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public ContactContext gender(GenderEnum gender) {
    this.gender = gender;
    return this;
  }

   /**
   * Gender of the contact person for this entity. 
   * @return gender
  **/
  @ApiModelProperty(value = "Gender of the contact person for this entity. ")
  public GenderEnum getGender() {
    return gender;
  }

  public void setGender(GenderEnum gender) {
    this.gender = gender;
  }

  public ContactContext addressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
    return this;
  }

   /**
   * Postal address of the contact person for this entity. 
   * @return addressLine1
  **/
  @ApiModelProperty(value = "Postal address of the contact person for this entity. ")
  public String getAddressLine1() {
    return addressLine1;
  }

  public void setAddressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
  }

  public ContactContext addressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
    return this;
  }

   /**
   * An additional optional line for the postal address. 
   * @return addressLine2
  **/
  @ApiModelProperty(value = "An additional optional line for the postal address. ")
  public String getAddressLine2() {
    return addressLine2;
  }

  public void setAddressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
  }

  public ContactContext city(String city) {
    this.city = city;
    return this;
  }

   /**
   * City of the contact person for this entity. 
   * @return city
  **/
  @ApiModelProperty(value = "City of the contact person for this entity. ")
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public ContactContext zipcode(String zipcode) {
    this.zipcode = zipcode;
    return this;
  }

   /**
   * Postal/ZIP code of the contact person for this entity. 
   * @return zipcode
  **/
  @ApiModelProperty(value = "Postal/ZIP code of the contact person for this entity. ")
  public String getZipcode() {
    return zipcode;
  }

  public void setZipcode(String zipcode) {
    this.zipcode = zipcode;
  }

  public ContactContext country(String country) {
    this.country = country;
    return this;
  }

   /**
   * Country of the contact person for this entity. 
   * @return country
  **/
  @ApiModelProperty(value = "Country of the contact person for this entity. ")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public ContactContext companyGroupName(String companyGroupName) {
    this.companyGroupName = companyGroupName;
    return this;
  }

   /**
   * Name of the company this entity belongs to. 
   * @return companyGroupName
  **/
  @ApiModelProperty(value = "Name of the company this entity belongs to. ")
  public String getCompanyGroupName() {
    return companyGroupName;
  }

  public void setCompanyGroupName(String companyGroupName) {
    this.companyGroupName = companyGroupName;
  }

  public ContactContext dateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

   /**
   * Date of birth of the contact person for this entity. 
   * @return dateOfBirth
  **/
  @ApiModelProperty(value = "Date of birth of the contact person for this entity. ")
  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public ContactContext email(String email) {
    this.email = email;
    return this;
  }

   /**
   * Contact email address for this entity. 
   * @return email
  **/
  @ApiModelProperty(value = "Contact email address for this entity. ")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public ContactContext phone(String phone) {
    this.phone = phone;
    return this;
  }

   /**
   * Contact phone for this entity. 
   * @return phone
  **/
  @ApiModelProperty(value = "Contact phone for this entity. ")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public ContactContext phoneOther(String phoneOther) {
    this.phoneOther = phoneOther;
    return this;
  }

   /**
   * Additional contact phone. 
   * @return phoneOther
  **/
  @ApiModelProperty(value = "Additional contact phone. ")
  public String getPhoneOther() {
    return phoneOther;
  }

  public void setPhoneOther(String phoneOther) {
    this.phoneOther = phoneOther;
  }

  public ContactContext fax(String fax) {
    this.fax = fax;
    return this;
  }

   /**
   * Contact fax number for this entity. 
   * @return fax
  **/
  @ApiModelProperty(value = "Contact fax number for this entity. ")
  public String getFax() {
    return fax;
  }

  public void setFax(String fax) {
    this.fax = fax;
  }

  public ContactContext icqNumber(String icqNumber) {
    this.icqNumber = icqNumber;
    return this;
  }

   /**
   * ICQ number of the contact person for this entity. 
   * @return icqNumber
  **/
  @ApiModelProperty(value = "ICQ number of the contact person for this entity. ")
  public String getIcqNumber() {
    return icqNumber;
  }

  public void setIcqNumber(String icqNumber) {
    this.icqNumber = icqNumber;
  }

  public ContactContext skypeName(String skypeName) {
    this.skypeName = skypeName;
    return this;
  }

   /**
   * Skype username of the contact person for this entity. 
   * @return skypeName
  **/
  @ApiModelProperty(value = "Skype username of the contact person for this entity. ")
  public String getSkypeName() {
    return skypeName;
  }

  public void setSkypeName(String skypeName) {
    this.skypeName = skypeName;
  }

  public ContactContext vat(String vat) {
    this.vat = vat;
    return this;
  }

   /**
   * VAT number of the contact person for this entity. 
   * @return vat
  **/
  @ApiModelProperty(value = "VAT number of the contact person for this entity. ")
  public String getVat() {
    return vat;
  }

  public void setVat(String vat) {
    this.vat = vat;
  }

  public ContactContext website(String website) {
    this.website = website;
    return this;
  }

   /**
   * Website URL of the contact person for this entity. 
   * @return website
  **/
  @ApiModelProperty(value = "Website URL of the contact person for this entity. ")
  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContactContext contactContext = (ContactContext) o;
    return Objects.equals(this.firstname, contactContext.firstname) &&
        Objects.equals(this.lastname, contactContext.lastname) &&
        Objects.equals(this.gender, contactContext.gender) &&
        Objects.equals(this.addressLine1, contactContext.addressLine1) &&
        Objects.equals(this.addressLine2, contactContext.addressLine2) &&
        Objects.equals(this.city, contactContext.city) &&
        Objects.equals(this.zipcode, contactContext.zipcode) &&
        Objects.equals(this.country, contactContext.country) &&
        Objects.equals(this.companyGroupName, contactContext.companyGroupName) &&
        Objects.equals(this.dateOfBirth, contactContext.dateOfBirth) &&
        Objects.equals(this.email, contactContext.email) &&
        Objects.equals(this.phone, contactContext.phone) &&
        Objects.equals(this.phoneOther, contactContext.phoneOther) &&
        Objects.equals(this.fax, contactContext.fax) &&
        Objects.equals(this.icqNumber, contactContext.icqNumber) &&
        Objects.equals(this.skypeName, contactContext.skypeName) &&
        Objects.equals(this.vat, contactContext.vat) &&
        Objects.equals(this.website, contactContext.website) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstname, lastname, gender, addressLine1, addressLine2, city, zipcode, country, companyGroupName, dateOfBirth, email, phone, phoneOther, fax, icqNumber, skypeName, vat, website, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContactContext {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    firstname: ").append(toIndentedString(firstname)).append("\n");
    sb.append("    lastname: ").append(toIndentedString(lastname)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    addressLine1: ").append(toIndentedString(addressLine1)).append("\n");
    sb.append("    addressLine2: ").append(toIndentedString(addressLine2)).append("\n");
    sb.append("    city: ").append(toIndentedString(city)).append("\n");
    sb.append("    zipcode: ").append(toIndentedString(zipcode)).append("\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
    sb.append("    companyGroupName: ").append(toIndentedString(companyGroupName)).append("\n");
    sb.append("    dateOfBirth: ").append(toIndentedString(dateOfBirth)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
    sb.append("    phoneOther: ").append(toIndentedString(phoneOther)).append("\n");
    sb.append("    fax: ").append(toIndentedString(fax)).append("\n");
    sb.append("    icqNumber: ").append(toIndentedString(icqNumber)).append("\n");
    sb.append("    skypeName: ").append(toIndentedString(skypeName)).append("\n");
    sb.append("    vat: ").append(toIndentedString(vat)).append("\n");
    sb.append("    website: ").append(toIndentedString(website)).append("\n");
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
