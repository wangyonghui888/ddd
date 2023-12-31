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
import java.util.ArrayList;
import java.util.List;

/**
 * Log configuration object 
 */
@ApiModel(description = "Log configuration object ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class LogContext extends Context {
  /**
   * Gets or Sets levels
   */
  @JsonAdapter(LevelsEnum.Adapter.class)
  public enum LevelsEnum {
    API_GET("API_GET"),
    
    API_POST("API_POST"),
    
    MANAGER_GET("MANAGER_GET"),
    
    MANAGER_POST("MANAGER_POST"),
    
    CLIENT_GET("CLIENT_GET"),
    
    CLIENT_POST("CLIENT_POST");

    private String value;

    LevelsEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static LevelsEnum fromValue(String text) {
      for (LevelsEnum b : LevelsEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<LevelsEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final LevelsEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public LevelsEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return LevelsEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("levels")
  private List<LevelsEnum> levels = null;

  @SerializedName("request")
  private Boolean request = null;

  @SerializedName("response")
  private Boolean response = null;

  public LogContext levels(List<LevelsEnum> levels) {
    this.levels = levels;
    return this;
  }

  public LogContext addLevelsItem(LevelsEnum levelsItem) {
    if (this.levels == null) {
      this.levels = new ArrayList<LevelsEnum>();
    }
    this.levels.add(levelsItem);
    return this;
  }

   /**
   * These levels define where and when a log is saved. If no level is selected then no log will be saved     Level            | Description --------------------- | --------------------------------------------------------------------   API_GET             | Third-party APIs (External/Event) that querying data   API_POST            | Third-party APIs (External/Event) that modified/deleted data   MANAGER_GET         | Manager API that querying data   MANAGER_POST        | Manager API that modified/deleted data   CLIENT_GET          | Client API that querying data   CLIENT_POST         | Client API that modified/deleted data 
   * @return levels
  **/
  @ApiModelProperty(value = "These levels define where and when a log is saved. If no level is selected then no log will be saved     Level            | Description --------------------- | --------------------------------------------------------------------   API_GET             | Third-party APIs (External/Event) that querying data   API_POST            | Third-party APIs (External/Event) that modified/deleted data   MANAGER_GET         | Manager API that querying data   MANAGER_POST        | Manager API that modified/deleted data   CLIENT_GET          | Client API that querying data   CLIENT_POST         | Client API that modified/deleted data ")
  public List<LevelsEnum> getLevels() {
    return levels;
  }

  public void setLevels(List<LevelsEnum> levels) {
    this.levels = levels;
  }

  public LogContext request(Boolean request) {
    this.request = request;
    return this;
  }

   /**
   * To use this field it is necessary to have at least one level. The request data will not be saved if this property is false. 
   * @return request
  **/
  @ApiModelProperty(value = "To use this field it is necessary to have at least one level. The request data will not be saved if this property is false. ")
  public Boolean isRequest() {
    return request;
  }

  public void setRequest(Boolean request) {
    this.request = request;
  }

  public LogContext response(Boolean response) {
    this.response = response;
    return this;
  }

   /**
   * To use this field it is necessary to have at least one level. The response data and code will not be saved if this property is false. 
   * @return response
  **/
  @ApiModelProperty(value = "To use this field it is necessary to have at least one level. The response data and code will not be saved if this property is false. ")
  public Boolean isResponse() {
    return response;
  }

  public void setResponse(Boolean response) {
    this.response = response;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogContext logContext = (LogContext) o;
    return Objects.equals(this.levels, logContext.levels) &&
        Objects.equals(this.request, logContext.request) &&
        Objects.equals(this.response, logContext.response) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(levels, request, response, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogContext {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    levels: ").append(toIndentedString(levels)).append("\n");
    sb.append("    request: ").append(toIndentedString(request)).append("\n");
    sb.append("    response: ").append(toIndentedString(response)).append("\n");
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

