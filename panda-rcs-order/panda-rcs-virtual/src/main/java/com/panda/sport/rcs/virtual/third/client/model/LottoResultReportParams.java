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
import com.panda.sport.rcs.virtual.third.client.model.ReportParams;
import com.panda.sport.rcs.virtual.third.client.model.ReportTarget;
import java.io.IOException;

/**
 * Internal lotto result report parameters
 */
@ApiModel(description = "Internal lotto result report parameters")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class LottoResultReportParams extends ReportParams {
  @SerializedName("produces")
  private String produces = "application/pdf";

  @SerializedName("playlistId")
  private Integer playlistId = null;

  public LottoResultReportParams produces(String produces) {
    this.produces = produces;
    return this;
  }

   /**
   * Produces
   * @return produces
  **/
  @ApiModelProperty(required = true, value = "Produces")
  public String getProduces() {
    return produces;
  }

  public void setProduces(String produces) {
    this.produces = produces;
  }

  public LottoResultReportParams playlistId(Integer playlistId) {
    this.playlistId = playlistId;
    return this;
  }

   /**
   * Playlist Id.
   * @return playlistId
  **/
  @ApiModelProperty(required = true, value = "Playlist Id.")
  public Integer getPlaylistId() {
    return playlistId;
  }

  public void setPlaylistId(Integer playlistId) {
    this.playlistId = playlistId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LottoResultReportParams lottoResultReportParams = (LottoResultReportParams) o;
    return Objects.equals(this.produces, lottoResultReportParams.produces) &&
        Objects.equals(this.playlistId, lottoResultReportParams.playlistId) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(produces, playlistId, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LottoResultReportParams {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    produces: ").append(toIndentedString(produces)).append("\n");
    sb.append("    playlistId: ").append(toIndentedString(playlistId)).append("\n");
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

