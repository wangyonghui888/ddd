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
 * Visibility level for this credential, in order to establish the permissions to read/write certain entities. LEVEL_0   Read/Write permission:  Itself and own descendants. LEVEL_1   Read/Write permission:  Parent and any of its descendants. LEVEL_2   Read/Write permission:  Grandparent and any of its descendants. CLIENT   Root entity: First ancestor that is marked as Client   Read/Write permission:  Root entity and any of its descendants. 
 */
@JsonAdapter(EntityCredentialVisibilityLevel.Adapter.class)
public enum EntityCredentialVisibilityLevel {
  
  LEVEL_0("LEVEL_0"),
  
  LEVEL_1("LEVEL_1"),
  
  LEVEL_2("LEVEL_2"),
  
  CLIENT("CLIENT");

  private String value;

  EntityCredentialVisibilityLevel(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  public static EntityCredentialVisibilityLevel fromValue(String text) {
    for (EntityCredentialVisibilityLevel b : EntityCredentialVisibilityLevel.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }

  public static class Adapter extends TypeAdapter<EntityCredentialVisibilityLevel> {
    @Override
    public void write(final JsonWriter jsonWriter, final EntityCredentialVisibilityLevel enumeration) throws IOException {
      jsonWriter.value(enumeration.getValue());
    }

    @Override
    public EntityCredentialVisibilityLevel read(final JsonReader jsonReader) throws IOException {
      String value = jsonReader.nextString();
      return EntityCredentialVisibilityLevel.fromValue(String.valueOf(value));
    }
  }
}
