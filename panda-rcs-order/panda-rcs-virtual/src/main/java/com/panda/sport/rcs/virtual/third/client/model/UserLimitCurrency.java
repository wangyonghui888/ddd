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
 * User Profile Currency Setting. 
 */
@ApiModel(description = "User Profile Currency Setting. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class UserLimitCurrency {
  @SerializedName("key")
  private String key = null;

  @SerializedName("maxStake")
  private Double maxStake = null;

  @SerializedName("minStake")
  private Double minStake = null;

  public UserLimitCurrency key(String key) {
    this.key = key;
    return this;
  }

   /**
   * Get key
   * @return key
  **/
  @ApiModelProperty(value = "")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public UserLimitCurrency maxStake(Double maxStake) {
    this.maxStake = maxStake;
    return this;
  }

   /**
   * User Max Stake. 
   * minimum: 0
   * @return maxStake
  **/
  @ApiModelProperty(value = "User Max Stake. ")
  public Double getMaxStake() {
    return maxStake;
  }

  public void setMaxStake(Double maxStake) {
    this.maxStake = maxStake;
  }

  public UserLimitCurrency minStake(Double minStake) {
    this.minStake = minStake;
    return this;
  }

   /**
   * User Min Stake. 
   * minimum: 0
   * @return minStake
  **/
  @ApiModelProperty(value = "User Min Stake. ")
  public Double getMinStake() {
    return minStake;
  }

  public void setMinStake(Double minStake) {
    this.minStake = minStake;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserLimitCurrency userLimitCurrency = (UserLimitCurrency) o;
    return Objects.equals(this.key, userLimitCurrency.key) &&
        Objects.equals(this.maxStake, userLimitCurrency.maxStake) &&
        Objects.equals(this.minStake, userLimitCurrency.minStake);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, maxStake, minStake);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserLimitCurrency {\n");
    
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    maxStake: ").append(toIndentedString(maxStake)).append("\n");
    sb.append("    minStake: ").append(toIndentedString(minStake)).append("\n");
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
