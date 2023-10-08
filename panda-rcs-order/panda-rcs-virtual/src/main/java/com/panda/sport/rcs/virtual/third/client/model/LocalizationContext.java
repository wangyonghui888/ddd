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
import com.panda.sport.rcs.virtual.third.client.model.LocalizationCurrencySettings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Localization of language, currencies and branding settings.  All settings are independent of game. 
 */
@ApiModel(description = "Localization of language, currencies and branding settings.  All settings are independent of game. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class LocalizationContext extends Context {
  @SerializedName("language")
  private String language = null;

  @SerializedName("skin")
  private String skin = null;

  @SerializedName("timezone")
  private String timezone = null;

  @SerializedName("defaultCurrency")
  private String defaultCurrency = null;

  @SerializedName("tags")
  private List<String> tags = null;

  @SerializedName("currencies")
  private List<LocalizationCurrencySettings> currencies = null;

  public LocalizationContext language(String language) {
    this.language = language;
    return this;
  }

   /**
   * Default translation language and regional conventions. It should follow locale notations as in  i18n conventions   https://www.npmjs.com/package/i18n  TODO add reference link or annex table.           
   * @return language
  **/
  @ApiModelProperty(value = "Default translation language and regional conventions. It should follow locale notations as in  i18n conventions   https://www.npmjs.com/package/i18n  TODO add reference link or annex table.           ")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public LocalizationContext skin(String skin) {
    this.skin = skin;
    return this;
  }

   /**
   * Custom branding to be used on product client.
   * @return skin
  **/
  @ApiModelProperty(value = "Custom branding to be used on product client.")
  public String getSkin() {
    return skin;
  }

  public void setSkin(String skin) {
    this.skin = skin;
  }

  public LocalizationContext timezone(String timezone) {
    this.timezone = timezone;
    return this;
  }

   /**
   * List of time zone abbreviations on: https://en.wikipedia.org/wiki/List_of_time_zone_abbreviations 
   * @return timezone
  **/
  @ApiModelProperty(value = "List of time zone abbreviations on: https://en.wikipedia.org/wiki/List_of_time_zone_abbreviations ")
  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public LocalizationContext defaultCurrency(String defaultCurrency) {
    this.defaultCurrency = defaultCurrency;
    return this;
  }

   /**
   * Default currency to convert currency values to on reports. In frontend, this value will be used as default currency in case no wallet is configured (wallet status is empty) 
   * @return defaultCurrency
  **/
  @ApiModelProperty(value = "Default currency to convert currency values to on reports. In frontend, this value will be used as default currency in case no wallet is configured (wallet status is empty) ")
  public String getDefaultCurrency() {
    return defaultCurrency;
  }

  public void setDefaultCurrency(String defaultCurrency) {
    this.defaultCurrency = defaultCurrency;
  }

  public LocalizationContext tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public LocalizationContext addTagsItem(String tagsItem) {
    if (this.tags == null) {
      this.tags = new ArrayList<String>();
    }
    this.tags.add(tagsItem);
    return this;
  }

   /**
   * Tag setting list
   * @return tags
  **/
  @ApiModelProperty(value = "Tag setting list")
  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public LocalizationContext currencies(List<LocalizationCurrencySettings> currencies) {
    this.currencies = currencies;
    return this;
  }

  public LocalizationContext addCurrenciesItem(LocalizationCurrencySettings currenciesItem) {
    if (this.currencies == null) {
      this.currencies = new ArrayList<LocalizationCurrencySettings>();
    }
    this.currencies.add(currenciesItem);
    return this;
  }

   /**
   * Custom configuration per enabled currency.
   * @return currencies
  **/
  @ApiModelProperty(value = "Custom configuration per enabled currency.")
  public List<LocalizationCurrencySettings> getCurrencies() {
    return currencies;
  }

  public void setCurrencies(List<LocalizationCurrencySettings> currencies) {
    this.currencies = currencies;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LocalizationContext localizationContext = (LocalizationContext) o;
    return Objects.equals(this.language, localizationContext.language) &&
        Objects.equals(this.skin, localizationContext.skin) &&
        Objects.equals(this.timezone, localizationContext.timezone) &&
        Objects.equals(this.defaultCurrency, localizationContext.defaultCurrency) &&
        Objects.equals(this.tags, localizationContext.tags) &&
        Objects.equals(this.currencies, localizationContext.currencies) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(language, skin, timezone, defaultCurrency, tags, currencies, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LocalizationContext {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    skin: ").append(toIndentedString(skin)).append("\n");
    sb.append("    timezone: ").append(toIndentedString(timezone)).append("\n");
    sb.append("    defaultCurrency: ").append(toIndentedString(defaultCurrency)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    currencies: ").append(toIndentedString(currencies)).append("\n");
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
