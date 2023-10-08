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
import com.panda.sport.rcs.virtual.third.client.model.RiskMgmtSettings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Risk Management Context 
 */
@ApiModel(description = "Risk Management Context ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class RiskManagementContext extends Context {
  @SerializedName("riskMgmtSettings")
  private List<RiskMgmtSettings> riskMgmtSettings = null;

  public RiskManagementContext riskMgmtSettings(List<RiskMgmtSettings> riskMgmtSettings) {
    this.riskMgmtSettings = riskMgmtSettings;
    return this;
  }

  public RiskManagementContext addRiskMgmtSettingsItem(RiskMgmtSettings riskMgmtSettingsItem) {
    if (this.riskMgmtSettings == null) {
      this.riskMgmtSettings = new ArrayList<RiskMgmtSettings>();
    }
    this.riskMgmtSettings.add(riskMgmtSettingsItem);
    return this;
  }

   /**
   * List of Risk Mgmt. settings. 
   * @return riskMgmtSettings
  **/
  @ApiModelProperty(value = "List of Risk Mgmt. settings. ")
  public List<RiskMgmtSettings> getRiskMgmtSettings() {
    return riskMgmtSettings;
  }

  public void setRiskMgmtSettings(List<RiskMgmtSettings> riskMgmtSettings) {
    this.riskMgmtSettings = riskMgmtSettings;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RiskManagementContext riskManagementContext = (RiskManagementContext) o;
    return Objects.equals(this.riskMgmtSettings, riskManagementContext.riskMgmtSettings) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(riskMgmtSettings, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RiskManagementContext {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    riskMgmtSettings: ").append(toIndentedString(riskMgmtSettings)).append("\n");
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

