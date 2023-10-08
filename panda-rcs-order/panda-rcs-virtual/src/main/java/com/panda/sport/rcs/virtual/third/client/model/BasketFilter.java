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
import com.panda.sport.rcs.virtual.third.client.model.Filter;
import java.io.IOException;

/**
 * Filter for Baskets competition 
 */
@ApiModel(description = "Filter for Baskets competition ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class BasketFilter extends Filter {
  @SerializedName("resourcePathRanking")
  private String resourcePathRanking = null;

  @SerializedName("resourcePathVideo")
  private String resourcePathVideo = null;

  @SerializedName("resourcePathCoverage")
  private String resourcePathCoverage = null;

  @SerializedName("contextVideo")
  private String contextVideo = null;

  public BasketFilter resourcePathRanking(String resourcePathRanking) {
    this.resourcePathRanking = resourcePathRanking;
    return this;
  }

   /**
   * Name of the resource file where team ranking are stored. 
   * @return resourcePathRanking
  **/
  @ApiModelProperty(value = "Name of the resource file where team ranking are stored. ")
  public String getResourcePathRanking() {
    return resourcePathRanking;
  }

  public void setResourcePathRanking(String resourcePathRanking) {
    this.resourcePathRanking = resourcePathRanking;
  }

  public BasketFilter resourcePathVideo(String resourcePathVideo) {
    this.resourcePathVideo = resourcePathVideo;
    return this;
  }

   /**
   * Name of the resource file where basket videos are stored. 
   * @return resourcePathVideo
  **/
  @ApiModelProperty(value = "Name of the resource file where basket videos are stored. ")
  public String getResourcePathVideo() {
    return resourcePathVideo;
  }

  public void setResourcePathVideo(String resourcePathVideo) {
    this.resourcePathVideo = resourcePathVideo;
  }

  public BasketFilter resourcePathCoverage(String resourcePathCoverage) {
    this.resourcePathCoverage = resourcePathCoverage;
    return this;
  }

   /**
   * Name of the resource file where coverage basket videos are stored.             
   * @return resourcePathCoverage
  **/
  @ApiModelProperty(value = "Name of the resource file where coverage basket videos are stored.             ")
  public String getResourcePathCoverage() {
    return resourcePathCoverage;
  }

  public void setResourcePathCoverage(String resourcePathCoverage) {
    this.resourcePathCoverage = resourcePathCoverage;
  }

  public BasketFilter contextVideo(String contextVideo) {
    this.contextVideo = contextVideo;
    return this;
  }

   /**
   * Identifier used to obtain the video metadata from a given result. 
   * @return contextVideo
  **/
  @ApiModelProperty(value = "Identifier used to obtain the video metadata from a given result. ")
  public String getContextVideo() {
    return contextVideo;
  }

  public void setContextVideo(String contextVideo) {
    this.contextVideo = contextVideo;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasketFilter basketFilter = (BasketFilter) o;
    return Objects.equals(this.resourcePathRanking, basketFilter.resourcePathRanking) &&
        Objects.equals(this.resourcePathVideo, basketFilter.resourcePathVideo) &&
        Objects.equals(this.resourcePathCoverage, basketFilter.resourcePathCoverage) &&
        Objects.equals(this.contextVideo, basketFilter.contextVideo) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resourcePathRanking, resourcePathVideo, resourcePathCoverage, contextVideo, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasketFilter {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    resourcePathRanking: ").append(toIndentedString(resourcePathRanking)).append("\n");
    sb.append("    resourcePathVideo: ").append(toIndentedString(resourcePathVideo)).append("\n");
    sb.append("    resourcePathCoverage: ").append(toIndentedString(resourcePathCoverage)).append("\n");
    sb.append("    contextVideo: ").append(toIndentedString(contextVideo)).append("\n");
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
