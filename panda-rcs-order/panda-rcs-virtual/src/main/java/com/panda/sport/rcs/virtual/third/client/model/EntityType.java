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
 * Entity types. For all types of entities supported 
 */
@JsonAdapter(EntityType.Adapter.class)
public enum EntityType {
  
  ACCOUNT("ACCOUNT"),
  
  CLIENT("CLIENT"),
  
  WALLET("WALLET"),
  
  USER("USER"),
  
  MANAGER("MANAGER"),
  
  API("API"),
  
  JACKPOT("JACKPOT");

  private String value;

  EntityType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  public static EntityType fromValue(String text) {
    for (EntityType b : EntityType.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }

  public static class Adapter extends TypeAdapter<EntityType> {
    @Override
    public void write(final JsonWriter jsonWriter, final EntityType enumeration) throws IOException {
      jsonWriter.value(enumeration.getValue());
    }

    @Override
    public EntityType read(final JsonReader jsonReader) throws IOException {
      String value = jsonReader.nextString();
      return EntityType.fromValue(String.valueOf(value));
    }
  }
}
