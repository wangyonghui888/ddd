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
import com.panda.sport.rcs.virtual.third.client.model.LogispinPrintProfileSettings;
import java.io.IOException;

/**
 * LogispinProfilesContext
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class LogispinProfilesContext extends Context {
  @SerializedName("print")
  private LogispinPrintProfileSettings print = null;

  public LogispinProfilesContext print(LogispinPrintProfileSettings print) {
    this.print = print;
    return this;
  }

   /**
   * Get print
   * @return print
  **/
  @ApiModelProperty(value = "")
  public LogispinPrintProfileSettings getPrint() {
    return print;
  }

  public void setPrint(LogispinPrintProfileSettings print) {
    this.print = print;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogispinProfilesContext logispinProfilesContext = (LogispinProfilesContext) o;
    return Objects.equals(this.print, logispinProfilesContext.print) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(print, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogispinProfilesContext {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    print: ").append(toIndentedString(print)).append("\n");
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

