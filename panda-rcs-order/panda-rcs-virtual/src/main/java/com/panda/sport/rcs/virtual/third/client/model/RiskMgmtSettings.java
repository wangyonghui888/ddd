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
import com.panda.sport.rcs.virtual.third.client.model.RiskMgmtTypeSettings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Risk Management Setting. 
 */
@ApiModel(description = "Risk Management Setting. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class RiskMgmtSettings {
  @SerializedName("riskMgmtId")
  private String riskMgmtId = null;

  @SerializedName("description")
  private String description = null;

  @SerializedName("targetId")
  private Integer targetId = null;

  /**
   * Defines how to calculate targetId, according to entity registering a ticket. Global, Local, Client, Level1, Level2, Level3.    TargetType        | Description ------------------- | ---------------------------------   Global            | Custom defined in targetId.   Local             | Solves target to current entity.   Client            | Solves target to first CLIENT in hierarchy.   Level1            | Solves target to 1st parent in hierarchy.   Level2            | Solves target to 2nd parent in hierarchy.   Level3            | Solves target to 3rd parent in hierarchy. 
   */
  @JsonAdapter(TargetTypeEnum.Adapter.class)
  public enum TargetTypeEnum {
    GLOBAL("GLOBAL"),
    
    CLIENT("CLIENT"),
    
    LOCAL("LOCAL"),
    
    LEVEL1("LEVEL1"),
    
    LEVEL2("LEVEL2"),
    
    LEVEL3("LEVEL3");

    private String value;

    TargetTypeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static TargetTypeEnum fromValue(String text) {
      for (TargetTypeEnum b : TargetTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<TargetTypeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final TargetTypeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public TargetTypeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return TargetTypeEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("targetType")
  private TargetTypeEnum targetType = null;

  @SerializedName("riskMgmtTypeSettings")
  private List<RiskMgmtTypeSettings> riskMgmtTypeSettings = null;

  public RiskMgmtSettings riskMgmtId(String riskMgmtId) {
    this.riskMgmtId = riskMgmtId;
    return this;
  }

   /**
   * Unique Id of Risk Mgmt. slot. 
   * @return riskMgmtId
  **/
  @ApiModelProperty(required = true, value = "Unique Id of Risk Mgmt. slot. ")
  public String getRiskMgmtId() {
    return riskMgmtId;
  }

  public void setRiskMgmtId(String riskMgmtId) {
    this.riskMgmtId = riskMgmtId;
  }

  public RiskMgmtSettings description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Human description of current risk mgmt. slot.
   * @return description
  **/
  @ApiModelProperty(value = "Human description of current risk mgmt. slot.")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public RiskMgmtSettings targetId(Integer targetId) {
    this.targetId = targetId;
    return this;
  }

   /**
   * Effective targetId. If not Global, it can optionally held the effective targetId, after evaluation of targetType. 
   * @return targetId
  **/
  @ApiModelProperty(value = "Effective targetId. If not Global, it can optionally held the effective targetId, after evaluation of targetType. ")
  public Integer getTargetId() {
    return targetId;
  }

  public void setTargetId(Integer targetId) {
    this.targetId = targetId;
  }

  public RiskMgmtSettings targetType(TargetTypeEnum targetType) {
    this.targetType = targetType;
    return this;
  }

   /**
   * Defines how to calculate targetId, according to entity registering a ticket. Global, Local, Client, Level1, Level2, Level3.    TargetType        | Description ------------------- | ---------------------------------   Global            | Custom defined in targetId.   Local             | Solves target to current entity.   Client            | Solves target to first CLIENT in hierarchy.   Level1            | Solves target to 1st parent in hierarchy.   Level2            | Solves target to 2nd parent in hierarchy.   Level3            | Solves target to 3rd parent in hierarchy. 
   * @return targetType
  **/
  @ApiModelProperty(value = "Defines how to calculate targetId, according to entity registering a ticket. Global, Local, Client, Level1, Level2, Level3.    TargetType        | Description ------------------- | ---------------------------------   Global            | Custom defined in targetId.   Local             | Solves target to current entity.   Client            | Solves target to first CLIENT in hierarchy.   Level1            | Solves target to 1st parent in hierarchy.   Level2            | Solves target to 2nd parent in hierarchy.   Level3            | Solves target to 3rd parent in hierarchy. ")
  public TargetTypeEnum getTargetType() {
    return targetType;
  }

  public void setTargetType(TargetTypeEnum targetType) {
    this.targetType = targetType;
  }

  public RiskMgmtSettings riskMgmtTypeSettings(List<RiskMgmtTypeSettings> riskMgmtTypeSettings) {
    this.riskMgmtTypeSettings = riskMgmtTypeSettings;
    return this;
  }

  public RiskMgmtSettings addRiskMgmtTypeSettingsItem(RiskMgmtTypeSettings riskMgmtTypeSettingsItem) {
    if (this.riskMgmtTypeSettings == null) {
      this.riskMgmtTypeSettings = new ArrayList<RiskMgmtTypeSettings>();
    }
    this.riskMgmtTypeSettings.add(riskMgmtTypeSettingsItem);
    return this;
  }

   /**
   * Custom settigns for risk mgmt. type. Not possible to add settings with same currency. 
   * @return riskMgmtTypeSettings
  **/
  @ApiModelProperty(value = "Custom settigns for risk mgmt. type. Not possible to add settings with same currency. ")
  public List<RiskMgmtTypeSettings> getRiskMgmtTypeSettings() {
    return riskMgmtTypeSettings;
  }

  public void setRiskMgmtTypeSettings(List<RiskMgmtTypeSettings> riskMgmtTypeSettings) {
    this.riskMgmtTypeSettings = riskMgmtTypeSettings;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RiskMgmtSettings riskMgmtSettings = (RiskMgmtSettings) o;
    return Objects.equals(this.riskMgmtId, riskMgmtSettings.riskMgmtId) &&
        Objects.equals(this.description, riskMgmtSettings.description) &&
        Objects.equals(this.targetId, riskMgmtSettings.targetId) &&
        Objects.equals(this.targetType, riskMgmtSettings.targetType) &&
        Objects.equals(this.riskMgmtTypeSettings, riskMgmtSettings.riskMgmtTypeSettings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(riskMgmtId, description, targetId, targetType, riskMgmtTypeSettings);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RiskMgmtSettings {\n");
    
    sb.append("    riskMgmtId: ").append(toIndentedString(riskMgmtId)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    targetId: ").append(toIndentedString(targetId)).append("\n");
    sb.append("    targetType: ").append(toIndentedString(targetType)).append("\n");
    sb.append("    riskMgmtTypeSettings: ").append(toIndentedString(riskMgmtTypeSettings)).append("\n");
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

