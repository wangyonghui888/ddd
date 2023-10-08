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
import java.util.ArrayList;
import java.util.List;

/**
 * Commission information about a playlist group. 
 */
@ApiModel(description = "Commission information about a playlist group. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class CommissionModule {
  @SerializedName("commissionModuleId")
  private String commissionModuleId = null;

  /**
   * Type of algorithm to be used in commission calculation. 
   */
  @JsonAdapter(AlgorithmTypeEnum.Adapter.class)
  public enum AlgorithmTypeEnum {
    STAKE("STAKE"),
    
    BALANCE("BALANCE");

    private String value;

    AlgorithmTypeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static AlgorithmTypeEnum fromValue(String text) {
      for (AlgorithmTypeEnum b : AlgorithmTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<AlgorithmTypeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final AlgorithmTypeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public AlgorithmTypeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return AlgorithmTypeEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("algorithmType")
  private AlgorithmTypeEnum algorithmType = null;

  @SerializedName("commission")
  private Double commission = null;

  @SerializedName("commissionModulePlaylistGroup")
  private List<Integer> commissionModulePlaylistGroup = null;

  public CommissionModule commissionModuleId(String commissionModuleId) {
    this.commissionModuleId = commissionModuleId;
    return this;
  }

   /**
   * Commission Module identifier to be used on monitor / container binding in viewers. 
   * @return commissionModuleId
  **/
  @ApiModelProperty(required = true, value = "Commission Module identifier to be used on monitor / container binding in viewers. ")
  public String getCommissionModuleId() {
    return commissionModuleId;
  }

  public void setCommissionModuleId(String commissionModuleId) {
    this.commissionModuleId = commissionModuleId;
  }

  public CommissionModule algorithmType(AlgorithmTypeEnum algorithmType) {
    this.algorithmType = algorithmType;
    return this;
  }

   /**
   * Type of algorithm to be used in commission calculation. 
   * @return algorithmType
  **/
  @ApiModelProperty(value = "Type of algorithm to be used in commission calculation. ")
  public AlgorithmTypeEnum getAlgorithmType() {
    return algorithmType;
  }

  public void setAlgorithmType(AlgorithmTypeEnum algorithmType) {
    this.algorithmType = algorithmType;
  }

  public CommissionModule commission(Double commission) {
    this.commission = commission;
    return this;
  }

   /**
   * Commission applied to selected playlist. 
   * minimum: 0
   * maximum: 100
   * @return commission
  **/
  @ApiModelProperty(value = "Commission applied to selected playlist. ")
  public Double getCommission() {
    return commission;
  }

  public void setCommission(Double commission) {
    this.commission = commission;
  }

  public CommissionModule commissionModulePlaylistGroup(List<Integer> commissionModulePlaylistGroup) {
    this.commissionModulePlaylistGroup = commissionModulePlaylistGroup;
    return this;
  }

  public CommissionModule addCommissionModulePlaylistGroupItem(Integer commissionModulePlaylistGroupItem) {
    if (this.commissionModulePlaylistGroup == null) {
      this.commissionModulePlaylistGroup = new ArrayList<Integer>();
    }
    this.commissionModulePlaylistGroup.add(commissionModulePlaylistGroupItem);
    return this;
  }

   /**
   * List of playlist that build the module playlist group. 
   * @return commissionModulePlaylistGroup
  **/
  @ApiModelProperty(value = "List of playlist that build the module playlist group. ")
  public List<Integer> getCommissionModulePlaylistGroup() {
    return commissionModulePlaylistGroup;
  }

  public void setCommissionModulePlaylistGroup(List<Integer> commissionModulePlaylistGroup) {
    this.commissionModulePlaylistGroup = commissionModulePlaylistGroup;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommissionModule commissionModule = (CommissionModule) o;
    return Objects.equals(this.commissionModuleId, commissionModule.commissionModuleId) &&
        Objects.equals(this.algorithmType, commissionModule.algorithmType) &&
        Objects.equals(this.commission, commissionModule.commission) &&
        Objects.equals(this.commissionModulePlaylistGroup, commissionModule.commissionModulePlaylistGroup);
  }

  @Override
  public int hashCode() {
    return Objects.hash(commissionModuleId, algorithmType, commission, commissionModulePlaylistGroup);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CommissionModule {\n");
    
    sb.append("    commissionModuleId: ").append(toIndentedString(commissionModuleId)).append("\n");
    sb.append("    algorithmType: ").append(toIndentedString(algorithmType)).append("\n");
    sb.append("    commission: ").append(toIndentedString(commission)).append("\n");
    sb.append("    commissionModulePlaylistGroup: ").append(toIndentedString(commissionModulePlaylistGroup)).append("\n");
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
