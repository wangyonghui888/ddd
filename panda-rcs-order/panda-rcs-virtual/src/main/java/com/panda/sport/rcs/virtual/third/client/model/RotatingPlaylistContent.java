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
import com.panda.sport.rcs.virtual.third.client.model.Content;
import com.panda.sport.rcs.virtual.third.client.model.PlaylistItemContent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Information of the rotation playlist content. 
 */
@ApiModel(description = "Information of the rotation playlist content. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class RotatingPlaylistContent extends Content {
  @SerializedName("contents")
  private List<PlaylistItemContent> contents = null;

  public RotatingPlaylistContent contents(List<PlaylistItemContent> contents) {
    this.contents = contents;
    return this;
  }

  public RotatingPlaylistContent addContentsItem(PlaylistItemContent contentsItem) {
    if (this.contents == null) {
      this.contents = new ArrayList<PlaylistItemContent>();
    }
    this.contents.add(contentsItem);
    return this;
  }

   /**
   * Get contents
   * @return contents
  **/
  @ApiModelProperty(value = "")
  public List<PlaylistItemContent> getContents() {
    return contents;
  }

  public void setContents(List<PlaylistItemContent> contents) {
    this.contents = contents;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RotatingPlaylistContent rotatingPlaylistContent = (RotatingPlaylistContent) o;
    return Objects.equals(this.contents, rotatingPlaylistContent.contents) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contents, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RotatingPlaylistContent {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    contents: ").append(toIndentedString(contents)).append("\n");
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

