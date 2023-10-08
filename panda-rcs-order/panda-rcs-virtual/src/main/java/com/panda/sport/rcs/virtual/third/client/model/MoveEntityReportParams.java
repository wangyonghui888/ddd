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
 * Move entity report parameters.  It is used to define the entity that will be the new parent of the entity to move. The entity that will be moved is the entityId of the ReportParams. 
 */
@ApiModel(description = "Move entity report parameters.  It is used to define the entity that will be the new parent of the entity to move. The entity that will be moved is the entityId of the ReportParams. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class MoveEntityReportParams extends ReportParams {
  @SerializedName("produces")
  private String produces = "text/csv";

  @SerializedName("destinationParentId")
  private Integer destinationParentId = null;

  public MoveEntityReportParams produces(String produces) {
    this.produces = produces;
    return this;
  }

   /**
   * Produces. 
   * @return produces
  **/
  @ApiModelProperty(required = true, value = "Produces. ")
  public String getProduces() {
    return produces;
  }

  public void setProduces(String produces) {
    this.produces = produces;
  }

  public MoveEntityReportParams destinationParentId(Integer destinationParentId) {
    this.destinationParentId = destinationParentId;
    return this;
  }

   /**
   * Entity id that will be the new parent of the origin entity, once it is moved. The origin entity that is going to be moved is the entityId of the ReportParams. 
   * @return destinationParentId
  **/
  @ApiModelProperty(required = true, value = "Entity id that will be the new parent of the origin entity, once it is moved. The origin entity that is going to be moved is the entityId of the ReportParams. ")
  public Integer getDestinationParentId() {
    return destinationParentId;
  }

  public void setDestinationParentId(Integer destinationParentId) {
    this.destinationParentId = destinationParentId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MoveEntityReportParams moveEntityReportParams = (MoveEntityReportParams) o;
    return Objects.equals(this.produces, moveEntityReportParams.produces) &&
        Objects.equals(this.destinationParentId, moveEntityReportParams.destinationParentId) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(produces, destinationParentId, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MoveEntityReportParams {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    produces: ").append(toIndentedString(produces)).append("\n");
    sb.append("    destinationParentId: ").append(toIndentedString(destinationParentId)).append("\n");
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
