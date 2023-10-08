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
import com.panda.sport.rcs.virtual.third.client.model.Entity;
import java.io.IOException;

/**
 * Hold identification and ownership of logged entites.  | Role        | Description                                                         | |------------ |---------------------------------------------------------------------| | **unit**    | Identifies account entity, with ownership of credit and tickets.    | | **staff**   | Identifies operator user, of bets placed on account.                | 
 */
@ApiModel(description = "Hold identification and ownership of logged entites.  | Role        | Description                                                         | |------------ |---------------------------------------------------------------------| | **unit**    | Identifies account entity, with ownership of credit and tickets.    | | **staff**   | Identifies operator user, of bets placed on account.                | ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class AuthResult {
  @SerializedName("unit")
  private Entity unit = null;

  @SerializedName("staff")
  private Entity staff = null;

  public AuthResult unit(Entity unit) {
    this.unit = unit;
    return this;
  }

   /**
   * Get unit
   * @return unit
  **/
  @ApiModelProperty(value = "")
  public Entity getUnit() {
    return unit;
  }

  public void setUnit(Entity unit) {
    this.unit = unit;
  }

  public AuthResult staff(Entity staff) {
    this.staff = staff;
    return this;
  }

   /**
   * Get staff
   * @return staff
  **/
  @ApiModelProperty(value = "")
  public Entity getStaff() {
    return staff;
  }

  public void setStaff(Entity staff) {
    this.staff = staff;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuthResult authResult = (AuthResult) o;
    return Objects.equals(this.unit, authResult.unit) &&
        Objects.equals(this.staff, authResult.staff);
  }

  @Override
  public int hashCode() {
    return Objects.hash(unit, staff);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthResult {\n");
    
    sb.append("    unit: ").append(toIndentedString(unit)).append("\n");
    sb.append("    staff: ").append(toIndentedString(staff)).append("\n");
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
