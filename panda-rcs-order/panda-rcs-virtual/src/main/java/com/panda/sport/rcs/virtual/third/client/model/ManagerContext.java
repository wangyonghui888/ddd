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
import com.panda.sport.rcs.virtual.third.client.model.PresetGroup;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager Context Settings 
 */
@ApiModel(description = "Manager Context Settings ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class ManagerContext extends Context {
  @SerializedName("language")
  private String language = null;

  @SerializedName("timezone")
  private String timezone = null;

  @SerializedName("currency")
  private String currency = null;

  @SerializedName("hideDeletedEntities")
  private Boolean hideDeletedEntities = null;

  @SerializedName("presetGroups")
  private List<PresetGroup> presetGroups = null;

  public ManagerContext language(String language) {
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

  public ManagerContext timezone(String timezone) {
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

  public ManagerContext currency(String currency) {
    this.currency = currency;
    return this;
  }

   /**
   * Default currency to convert currency values to on reports.
   * @return currency
  **/
  @ApiModelProperty(value = "Default currency to convert currency values to on reports.")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public ManagerContext hideDeletedEntities(Boolean hideDeletedEntities) {
    this.hideDeletedEntities = hideDeletedEntities;
    return this;
  }

   /**
   * Boolean property that in case of being true hides the deleted entities. 
   * @return hideDeletedEntities
  **/
  @ApiModelProperty(value = "Boolean property that in case of being true hides the deleted entities. ")
  public Boolean isHideDeletedEntities() {
    return hideDeletedEntities;
  }

  public void setHideDeletedEntities(Boolean hideDeletedEntities) {
    this.hideDeletedEntities = hideDeletedEntities;
  }

  public ManagerContext presetGroups(List<PresetGroup> presetGroups) {
    this.presetGroups = presetGroups;
    return this;
  }

  public ManagerContext addPresetGroupsItem(PresetGroup presetGroupsItem) {
    if (this.presetGroups == null) {
      this.presetGroups = new ArrayList<PresetGroup>();
    }
    this.presetGroups.add(presetGroupsItem);
    return this;
  }

   /**
   * Presets configurations grouped 
   * @return presetGroups
  **/
  @ApiModelProperty(value = "Presets configurations grouped ")
  public List<PresetGroup> getPresetGroups() {
    return presetGroups;
  }

  public void setPresetGroups(List<PresetGroup> presetGroups) {
    this.presetGroups = presetGroups;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ManagerContext managerContext = (ManagerContext) o;
    return Objects.equals(this.language, managerContext.language) &&
        Objects.equals(this.timezone, managerContext.timezone) &&
        Objects.equals(this.currency, managerContext.currency) &&
        Objects.equals(this.hideDeletedEntities, managerContext.hideDeletedEntities) &&
        Objects.equals(this.presetGroups, managerContext.presetGroups) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(language, timezone, currency, hideDeletedEntities, presetGroups, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ManagerContext {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    timezone: ").append(toIndentedString(timezone)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    hideDeletedEntities: ").append(toIndentedString(hideDeletedEntities)).append("\n");
    sb.append("    presetGroups: ").append(toIndentedString(presetGroups)).append("\n");
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

