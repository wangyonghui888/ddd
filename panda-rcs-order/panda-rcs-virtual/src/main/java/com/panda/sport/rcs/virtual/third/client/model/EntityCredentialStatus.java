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
import io.swagger.annotations.ApiModel;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Credentials can be enabled / disabled on request,  or can be invalidated due to limit validation date,  or because any other automated automate invalidation policy by 3rd parties, without APi interaction. 
 */
@JsonAdapter(EntityCredentialStatus.Adapter.class)
public enum EntityCredentialStatus {
  
  ENABLED("ENABLED"),
  
  DISABLED("DISABLED"),
  
  INVALID("INVALID"),
  
  DELETED("DELETED");

  private String value;

  EntityCredentialStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  public static EntityCredentialStatus fromValue(String text) {
    for (EntityCredentialStatus b : EntityCredentialStatus.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }

  public static class Adapter extends TypeAdapter<EntityCredentialStatus> {
    @Override
    public void write(final JsonWriter jsonWriter, final EntityCredentialStatus enumeration) throws IOException {
      jsonWriter.value(enumeration.getValue());
    }

    @Override
    public EntityCredentialStatus read(final JsonReader jsonReader) throws IOException {
      String value = jsonReader.nextString();
      return EntityCredentialStatus.fromValue(String.valueOf(value));
    }
  }
}

