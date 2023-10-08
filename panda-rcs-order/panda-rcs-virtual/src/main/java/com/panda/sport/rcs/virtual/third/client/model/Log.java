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
 * Log
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class Log {
  @SerializedName("processId")
  private String processId = null;

  @SerializedName("transactionId")
  private String transactionId = null;

  @SerializedName("parentId")
  private String parentId = null;

  @SerializedName("entityId")
  private Integer entityId = null;

  @SerializedName("credentialId")
  private Integer credentialId = null;

  @SerializedName("sessionId")
  private Long sessionId = null;

  @SerializedName("ipAddress")
  private String ipAddress = null;

  @SerializedName("timest")
  private Long timest = null;

  @SerializedName("targetId")
  private Integer targetId = null;

  /**
   * Could be EVENT, MANAGER, CLIENT, EXTERNAL or SYSTEM
   */
  @JsonAdapter(TypeEnum.Adapter.class)
  public enum TypeEnum {
    EVENT("EVENT"),
    
    MANAGER("MANAGER"),
    
    CLIENT("CLIENT"),
    
    EXTERNAL("EXTERNAL"),
    
    SYSTEM("SYSTEM");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static TypeEnum fromValue(String text) {
      for (TypeEnum b : TypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<TypeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final TypeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public TypeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return TypeEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("type")
  private TypeEnum type = null;

  /**
   * Log level applied. It defines when and where the log is persisted. See LogContext 
   */
  @JsonAdapter(LevelEnum.Adapter.class)
  public enum LevelEnum {
    API_GET("API_GET"),
    
    API_POST("API_POST"),
    
    MANAGER_GET("MANAGER_GET"),
    
    MANAGER_POST("MANAGER_POST"),
    
    CLIENT_GET("CLIENT_GET"),
    
    CLIENT_POST("CLIENT_POST");

    private String value;

    LevelEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static LevelEnum fromValue(String text) {
      for (LevelEnum b : LevelEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<LevelEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final LevelEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public LevelEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return LevelEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("level")
  private LevelEnum level = null;

  @SerializedName("method")
  private String method = null;

  @SerializedName("stage")
  private String stage = null;

  @SerializedName("description")
  private String description = null;

  @SerializedName("params")
  private String params = null;

  @SerializedName("previousValue")
  private String previousValue = null;

  @SerializedName("resultCode")
  private String resultCode = null;

  @SerializedName("resultData")
  private String resultData = null;

  @SerializedName("executionTime")
  private Long executionTime = null;

  public Log processId(String processId) {
    this.processId = processId;
    return this;
  }

   /**
   * GUID of current thread, identify server+start_time_ms+threadId
   * @return processId
  **/
  @ApiModelProperty(value = "GUID of current thread, identify server+start_time_ms+threadId")
  public String getProcessId() {
    return processId;
  }

  public void setProcessId(String processId) {
    this.processId = processId;
  }

  public Log transactionId(String transactionId) {
    this.transactionId = transactionId;
    return this;
  }

   /**
   * The unique transaction identifier. GUID of current transaction, UUID+start_time_ms+threadId
   * @return transactionId
  **/
  @ApiModelProperty(value = "The unique transaction identifier. GUID of current transaction, UUID+start_time_ms+threadId")
  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public Log parentId(String parentId) {
    this.parentId = parentId;
    return this;
  }

   /**
   * Link to the parent log entry, for transaction based entries. Parent&#39;s transactionId.
   * @return parentId
  **/
  @ApiModelProperty(value = "Link to the parent log entry, for transaction based entries. Parent's transactionId.")
  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public Log entityId(Integer entityId) {
    this.entityId = entityId;
    return this;
  }

   /**
   * Entity identifier.
   * @return entityId
  **/
  @ApiModelProperty(value = "Entity identifier.")
  public Integer getEntityId() {
    return entityId;
  }

  public void setEntityId(Integer entityId) {
    this.entityId = entityId;
  }

  public Log credentialId(Integer credentialId) {
    this.credentialId = credentialId;
    return this;
  }

   /**
   * Depends on credentials used..
   * @return credentialId
  **/
  @ApiModelProperty(value = "Depends on credentials used..")
  public Integer getCredentialId() {
    return credentialId;
  }

  public void setCredentialId(Integer credentialId) {
    this.credentialId = credentialId;
  }

  public Log sessionId(Long sessionId) {
    this.sessionId = sessionId;
    return this;
  }

   /**
   * The user session identifier.
   * @return sessionId
  **/
  @ApiModelProperty(value = "The user session identifier.")
  public Long getSessionId() {
    return sessionId;
  }

  public void setSessionId(Long sessionId) {
    this.sessionId = sessionId;
  }

  public Log ipAddress(String ipAddress) {
    this.ipAddress = ipAddress;
    return this;
  }

   /**
   * The ip address of the log operation.
   * @return ipAddress
  **/
  @ApiModelProperty(value = "The ip address of the log operation.")
  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public Log timest(Long timest) {
    this.timest = timest;
    return this;
  }

   /**
   * The timestamp.
   * @return timest
  **/
  @ApiModelProperty(value = "The timestamp.")
  public Long getTimest() {
    return timest;
  }

  public void setTimest(Long timest) {
    this.timest = timest;
  }

  public Log targetId(Integer targetId) {
    this.targetId = targetId;
    return this;
  }

   /**
   * Entity identifier of target
   * @return targetId
  **/
  @ApiModelProperty(value = "Entity identifier of target")
  public Integer getTargetId() {
    return targetId;
  }

  public void setTargetId(Integer targetId) {
    this.targetId = targetId;
  }

  public Log type(TypeEnum type) {
    this.type = type;
    return this;
  }

   /**
   * Could be EVENT, MANAGER, CLIENT, EXTERNAL or SYSTEM
   * @return type
  **/
  @ApiModelProperty(value = "Could be EVENT, MANAGER, CLIENT, EXTERNAL or SYSTEM")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public Log level(LevelEnum level) {
    this.level = level;
    return this;
  }

   /**
   * Log level applied. It defines when and where the log is persisted. See LogContext 
   * @return level
  **/
  @ApiModelProperty(value = "Log level applied. It defines when and where the log is persisted. See LogContext ")
  public LevelEnum getLevel() {
    return level;
  }

  public void setLevel(LevelEnum level) {
    this.level = level;
  }

  public Log method(String method) {
    this.method = method;
    return this;
  }

   /**
   * Name of method.
   * @return method
  **/
  @ApiModelProperty(value = "Name of method.")
  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public Log stage(String stage) {
    this.stage = stage;
    return this;
  }

   /**
   * Internal step, inside the method.
   * @return stage
  **/
  @ApiModelProperty(value = "Internal step, inside the method.")
  public String getStage() {
    return stage;
  }

  public void setStage(String stage) {
    this.stage = stage;
  }

  public Log description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Human readable description.
   * @return description
  **/
  @ApiModelProperty(value = "Human readable description.")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Log params(String params) {
    this.params = params;
    return this;
  }

   /**
   * The param or object used in method.
   * @return params
  **/
  @ApiModelProperty(value = "The param or object used in method.")
  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public Log previousValue(String previousValue) {
    this.previousValue = previousValue;
    return this;
  }

   /**
   * The Object/Settings Json value, used to restore operation when possible.
   * @return previousValue
  **/
  @ApiModelProperty(value = "The Object/Settings Json value, used to restore operation when possible.")
  public String getPreviousValue() {
    return previousValue;
  }

  public void setPreviousValue(String previousValue) {
    this.previousValue = previousValue;
  }

  public Log resultCode(String resultCode) {
    this.resultCode = resultCode;
    return this;
  }

   /**
   * Optional user code of operation.
   * @return resultCode
  **/
  @ApiModelProperty(value = "Optional user code of operation.")
  public String getResultCode() {
    return resultCode;
  }

  public void setResultCode(String resultCode) {
    this.resultCode = resultCode;
  }

  public Log resultData(String resultData) {
    this.resultData = resultData;
    return this;
  }

   /**
   * Optional result of operation as JSON.
   * @return resultData
  **/
  @ApiModelProperty(value = "Optional result of operation as JSON.")
  public String getResultData() {
    return resultData;
  }

  public void setResultData(String resultData) {
    this.resultData = resultData;
  }

  public Log executionTime(Long executionTime) {
    this.executionTime = executionTime;
    return this;
  }

   /**
   * Execution time in milliseconds. Optional.
   * @return executionTime
  **/
  @ApiModelProperty(value = "Execution time in milliseconds. Optional.")
  public Long getExecutionTime() {
    return executionTime;
  }

  public void setExecutionTime(Long executionTime) {
    this.executionTime = executionTime;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Log log = (Log) o;
    return Objects.equals(this.processId, log.processId) &&
        Objects.equals(this.transactionId, log.transactionId) &&
        Objects.equals(this.parentId, log.parentId) &&
        Objects.equals(this.entityId, log.entityId) &&
        Objects.equals(this.credentialId, log.credentialId) &&
        Objects.equals(this.sessionId, log.sessionId) &&
        Objects.equals(this.ipAddress, log.ipAddress) &&
        Objects.equals(this.timest, log.timest) &&
        Objects.equals(this.targetId, log.targetId) &&
        Objects.equals(this.type, log.type) &&
        Objects.equals(this.level, log.level) &&
        Objects.equals(this.method, log.method) &&
        Objects.equals(this.stage, log.stage) &&
        Objects.equals(this.description, log.description) &&
        Objects.equals(this.params, log.params) &&
        Objects.equals(this.previousValue, log.previousValue) &&
        Objects.equals(this.resultCode, log.resultCode) &&
        Objects.equals(this.resultData, log.resultData) &&
        Objects.equals(this.executionTime, log.executionTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(processId, transactionId, parentId, entityId, credentialId, sessionId, ipAddress, timest, targetId, type, level, method, stage, description, params, previousValue, resultCode, resultData, executionTime);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Log {\n");
    
    sb.append("    processId: ").append(toIndentedString(processId)).append("\n");
    sb.append("    transactionId: ").append(toIndentedString(transactionId)).append("\n");
    sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
    sb.append("    entityId: ").append(toIndentedString(entityId)).append("\n");
    sb.append("    credentialId: ").append(toIndentedString(credentialId)).append("\n");
    sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
    sb.append("    ipAddress: ").append(toIndentedString(ipAddress)).append("\n");
    sb.append("    timest: ").append(toIndentedString(timest)).append("\n");
    sb.append("    targetId: ").append(toIndentedString(targetId)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    level: ").append(toIndentedString(level)).append("\n");
    sb.append("    method: ").append(toIndentedString(method)).append("\n");
    sb.append("    stage: ").append(toIndentedString(stage)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    params: ").append(toIndentedString(params)).append("\n");
    sb.append("    previousValue: ").append(toIndentedString(previousValue)).append("\n");
    sb.append("    resultCode: ").append(toIndentedString(resultCode)).append("\n");
    sb.append("    resultData: ").append(toIndentedString(resultData)).append("\n");
    sb.append("    executionTime: ").append(toIndentedString(executionTime)).append("\n");
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
