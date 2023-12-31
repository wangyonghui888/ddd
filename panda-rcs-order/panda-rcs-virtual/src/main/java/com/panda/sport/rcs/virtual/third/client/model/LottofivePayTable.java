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
 * Information about the pay table of Lotto Five. 
 */
@ApiModel(description = "Information about the pay table of Lotto Five. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-25T13:53:10.996Z")



public class LottofivePayTable {
  @SerializedName("n1")
  private Double n1 = null;

  @SerializedName("n2")
  private Double n2 = null;

  @SerializedName("n3")
  private Double n3 = null;

  @SerializedName("n4")
  private Double n4 = null;

  @SerializedName("n5")
  private Double n5 = null;

  @SerializedName("n1_machine_numbers")
  private Double n1MachineNumbers = null;

  @SerializedName("n2_machine_numbers")
  private Double n2MachineNumbers = null;

  @SerializedName("n3_machine_numbers")
  private Double n3MachineNumbers = null;

  @SerializedName("n4_machine_numbers")
  private Double n4MachineNumbers = null;

  @SerializedName("n5_machine_numbers")
  private Double n5MachineNumbers = null;

  @SerializedName("first_drawn")
  private Double firstDrawn = null;

  @SerializedName("first_drawn_machine_numbers")
  private Double firstDrawnMachineNumbers = null;

  @SerializedName("green_4_plus")
  private Double green4Plus = null;

  @SerializedName("green_5")
  private Double green5 = null;

  @SerializedName("red_4_plus")
  private Double red4Plus = null;

  @SerializedName("red_5")
  private Double red5 = null;

  @SerializedName("yellow_4_plus")
  private Double yellow4Plus = null;

  @SerializedName("yellow_5")
  private Double yellow5 = null;

  public LottofivePayTable n1(Double n1) {
    this.n1 = n1;
    return this;
  }

   /**
   * The odd value for bet id n1.
   * minimum: 1
   * @return n1
  **/
  @ApiModelProperty(value = "The odd value for bet id n1.")
  public Double getN1() {
    return n1;
  }

  public void setN1(Double n1) {
    this.n1 = n1;
  }

  public LottofivePayTable n2(Double n2) {
    this.n2 = n2;
    return this;
  }

   /**
   * The odd value for bet id n2.
   * minimum: 1
   * @return n2
  **/
  @ApiModelProperty(value = "The odd value for bet id n2.")
  public Double getN2() {
    return n2;
  }

  public void setN2(Double n2) {
    this.n2 = n2;
  }

  public LottofivePayTable n3(Double n3) {
    this.n3 = n3;
    return this;
  }

   /**
   * The odd value for bet id n3.
   * minimum: 1
   * @return n3
  **/
  @ApiModelProperty(value = "The odd value for bet id n3.")
  public Double getN3() {
    return n3;
  }

  public void setN3(Double n3) {
    this.n3 = n3;
  }

  public LottofivePayTable n4(Double n4) {
    this.n4 = n4;
    return this;
  }

   /**
   * The odd value for bet id n4.
   * minimum: 1
   * @return n4
  **/
  @ApiModelProperty(value = "The odd value for bet id n4.")
  public Double getN4() {
    return n4;
  }

  public void setN4(Double n4) {
    this.n4 = n4;
  }

  public LottofivePayTable n5(Double n5) {
    this.n5 = n5;
    return this;
  }

   /**
   * The odd value for bet id n5.
   * minimum: 1
   * @return n5
  **/
  @ApiModelProperty(value = "The odd value for bet id n5.")
  public Double getN5() {
    return n5;
  }

  public void setN5(Double n5) {
    this.n5 = n5;
  }

  public LottofivePayTable n1MachineNumbers(Double n1MachineNumbers) {
    this.n1MachineNumbers = n1MachineNumbers;
    return this;
  }

   /**
   * The odd value for money back if selection matches the machine numbers for bet id n1.
   * minimum: 0
   * @return n1MachineNumbers
  **/
  @ApiModelProperty(value = "The odd value for money back if selection matches the machine numbers for bet id n1.")
  public Double getN1MachineNumbers() {
    return n1MachineNumbers;
  }

  public void setN1MachineNumbers(Double n1MachineNumbers) {
    this.n1MachineNumbers = n1MachineNumbers;
  }

  public LottofivePayTable n2MachineNumbers(Double n2MachineNumbers) {
    this.n2MachineNumbers = n2MachineNumbers;
    return this;
  }

   /**
   * The odd value for money back if selection matches the machine numbers for bet id n2.
   * minimum: 0
   * @return n2MachineNumbers
  **/
  @ApiModelProperty(value = "The odd value for money back if selection matches the machine numbers for bet id n2.")
  public Double getN2MachineNumbers() {
    return n2MachineNumbers;
  }

  public void setN2MachineNumbers(Double n2MachineNumbers) {
    this.n2MachineNumbers = n2MachineNumbers;
  }

  public LottofivePayTable n3MachineNumbers(Double n3MachineNumbers) {
    this.n3MachineNumbers = n3MachineNumbers;
    return this;
  }

   /**
   * The odd value for money back if selection matches the machine numbers for bet id n3.
   * minimum: 0
   * @return n3MachineNumbers
  **/
  @ApiModelProperty(value = "The odd value for money back if selection matches the machine numbers for bet id n3.")
  public Double getN3MachineNumbers() {
    return n3MachineNumbers;
  }

  public void setN3MachineNumbers(Double n3MachineNumbers) {
    this.n3MachineNumbers = n3MachineNumbers;
  }

  public LottofivePayTable n4MachineNumbers(Double n4MachineNumbers) {
    this.n4MachineNumbers = n4MachineNumbers;
    return this;
  }

   /**
   * The odd value for money back if selection matches the machine numbers for bet id n4.
   * minimum: 0
   * @return n4MachineNumbers
  **/
  @ApiModelProperty(value = "The odd value for money back if selection matches the machine numbers for bet id n4.")
  public Double getN4MachineNumbers() {
    return n4MachineNumbers;
  }

  public void setN4MachineNumbers(Double n4MachineNumbers) {
    this.n4MachineNumbers = n4MachineNumbers;
  }

  public LottofivePayTable n5MachineNumbers(Double n5MachineNumbers) {
    this.n5MachineNumbers = n5MachineNumbers;
    return this;
  }

   /**
   * The odd value for money back if selection matches the machine numbers for bet id n5.
   * minimum: 0
   * @return n5MachineNumbers
  **/
  @ApiModelProperty(value = "The odd value for money back if selection matches the machine numbers for bet id n5.")
  public Double getN5MachineNumbers() {
    return n5MachineNumbers;
  }

  public void setN5MachineNumbers(Double n5MachineNumbers) {
    this.n5MachineNumbers = n5MachineNumbers;
  }

  public LottofivePayTable firstDrawn(Double firstDrawn) {
    this.firstDrawn = firstDrawn;
    return this;
  }

   /**
   * The odd value for bet id first.
   * minimum: 1
   * @return firstDrawn
  **/
  @ApiModelProperty(value = "The odd value for bet id first.")
  public Double getFirstDrawn() {
    return firstDrawn;
  }

  public void setFirstDrawn(Double firstDrawn) {
    this.firstDrawn = firstDrawn;
  }

  public LottofivePayTable firstDrawnMachineNumbers(Double firstDrawnMachineNumbers) {
    this.firstDrawnMachineNumbers = firstDrawnMachineNumbers;
    return this;
  }

   /**
   * The odd value for monety back if selection matches the machine number for bet id first.
   * minimum: 1
   * @return firstDrawnMachineNumbers
  **/
  @ApiModelProperty(value = "The odd value for monety back if selection matches the machine number for bet id first.")
  public Double getFirstDrawnMachineNumbers() {
    return firstDrawnMachineNumbers;
  }

  public void setFirstDrawnMachineNumbers(Double firstDrawnMachineNumbers) {
    this.firstDrawnMachineNumbers = firstDrawnMachineNumbers;
  }

  public LottofivePayTable green4Plus(Double green4Plus) {
    this.green4Plus = green4Plus;
    return this;
  }

   /**
   * The odd value for monety back if selection matches the bet id green 4 plus.
   * minimum: 1
   * @return green4Plus
  **/
  @ApiModelProperty(value = "The odd value for monety back if selection matches the bet id green 4 plus.")
  public Double getGreen4Plus() {
    return green4Plus;
  }

  public void setGreen4Plus(Double green4Plus) {
    this.green4Plus = green4Plus;
  }

  public LottofivePayTable green5(Double green5) {
    this.green5 = green5;
    return this;
  }

   /**
   * The odd value for monety back if selection matches the bet id green 5.
   * minimum: 1
   * @return green5
  **/
  @ApiModelProperty(value = "The odd value for monety back if selection matches the bet id green 5.")
  public Double getGreen5() {
    return green5;
  }

  public void setGreen5(Double green5) {
    this.green5 = green5;
  }

  public LottofivePayTable red4Plus(Double red4Plus) {
    this.red4Plus = red4Plus;
    return this;
  }

   /**
   * The odd value for monety back if selection matches the bet id red 4 plus.
   * minimum: 1
   * @return red4Plus
  **/
  @ApiModelProperty(value = "The odd value for monety back if selection matches the bet id red 4 plus.")
  public Double getRed4Plus() {
    return red4Plus;
  }

  public void setRed4Plus(Double red4Plus) {
    this.red4Plus = red4Plus;
  }

  public LottofivePayTable red5(Double red5) {
    this.red5 = red5;
    return this;
  }

   /**
   * The odd value for monety back if selection matches the bet id red 5.
   * minimum: 1
   * @return red5
  **/
  @ApiModelProperty(value = "The odd value for monety back if selection matches the bet id red 5.")
  public Double getRed5() {
    return red5;
  }

  public void setRed5(Double red5) {
    this.red5 = red5;
  }

  public LottofivePayTable yellow4Plus(Double yellow4Plus) {
    this.yellow4Plus = yellow4Plus;
    return this;
  }

   /**
   * The odd value for monety back if selection matches the bet id yellow 4 plus.
   * minimum: 1
   * @return yellow4Plus
  **/
  @ApiModelProperty(value = "The odd value for monety back if selection matches the bet id yellow 4 plus.")
  public Double getYellow4Plus() {
    return yellow4Plus;
  }

  public void setYellow4Plus(Double yellow4Plus) {
    this.yellow4Plus = yellow4Plus;
  }

  public LottofivePayTable yellow5(Double yellow5) {
    this.yellow5 = yellow5;
    return this;
  }

   /**
   * The odd value for monety back if selection matches the bet id yellow 5.
   * minimum: 1
   * @return yellow5
  **/
  @ApiModelProperty(value = "The odd value for monety back if selection matches the bet id yellow 5.")
  public Double getYellow5() {
    return yellow5;
  }

  public void setYellow5(Double yellow5) {
    this.yellow5 = yellow5;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LottofivePayTable lottofivePayTable = (LottofivePayTable) o;
    return Objects.equals(this.n1, lottofivePayTable.n1) &&
        Objects.equals(this.n2, lottofivePayTable.n2) &&
        Objects.equals(this.n3, lottofivePayTable.n3) &&
        Objects.equals(this.n4, lottofivePayTable.n4) &&
        Objects.equals(this.n5, lottofivePayTable.n5) &&
        Objects.equals(this.n1MachineNumbers, lottofivePayTable.n1MachineNumbers) &&
        Objects.equals(this.n2MachineNumbers, lottofivePayTable.n2MachineNumbers) &&
        Objects.equals(this.n3MachineNumbers, lottofivePayTable.n3MachineNumbers) &&
        Objects.equals(this.n4MachineNumbers, lottofivePayTable.n4MachineNumbers) &&
        Objects.equals(this.n5MachineNumbers, lottofivePayTable.n5MachineNumbers) &&
        Objects.equals(this.firstDrawn, lottofivePayTable.firstDrawn) &&
        Objects.equals(this.firstDrawnMachineNumbers, lottofivePayTable.firstDrawnMachineNumbers) &&
        Objects.equals(this.green4Plus, lottofivePayTable.green4Plus) &&
        Objects.equals(this.green5, lottofivePayTable.green5) &&
        Objects.equals(this.red4Plus, lottofivePayTable.red4Plus) &&
        Objects.equals(this.red5, lottofivePayTable.red5) &&
        Objects.equals(this.yellow4Plus, lottofivePayTable.yellow4Plus) &&
        Objects.equals(this.yellow5, lottofivePayTable.yellow5);
  }

  @Override
  public int hashCode() {
    return Objects.hash(n1, n2, n3, n4, n5, n1MachineNumbers, n2MachineNumbers, n3MachineNumbers, n4MachineNumbers, n5MachineNumbers, firstDrawn, firstDrawnMachineNumbers, green4Plus, green5, red4Plus, red5, yellow4Plus, yellow5);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LottofivePayTable {\n");
    
    sb.append("    n1: ").append(toIndentedString(n1)).append("\n");
    sb.append("    n2: ").append(toIndentedString(n2)).append("\n");
    sb.append("    n3: ").append(toIndentedString(n3)).append("\n");
    sb.append("    n4: ").append(toIndentedString(n4)).append("\n");
    sb.append("    n5: ").append(toIndentedString(n5)).append("\n");
    sb.append("    n1MachineNumbers: ").append(toIndentedString(n1MachineNumbers)).append("\n");
    sb.append("    n2MachineNumbers: ").append(toIndentedString(n2MachineNumbers)).append("\n");
    sb.append("    n3MachineNumbers: ").append(toIndentedString(n3MachineNumbers)).append("\n");
    sb.append("    n4MachineNumbers: ").append(toIndentedString(n4MachineNumbers)).append("\n");
    sb.append("    n5MachineNumbers: ").append(toIndentedString(n5MachineNumbers)).append("\n");
    sb.append("    firstDrawn: ").append(toIndentedString(firstDrawn)).append("\n");
    sb.append("    firstDrawnMachineNumbers: ").append(toIndentedString(firstDrawnMachineNumbers)).append("\n");
    sb.append("    green4Plus: ").append(toIndentedString(green4Plus)).append("\n");
    sb.append("    green5: ").append(toIndentedString(green5)).append("\n");
    sb.append("    red4Plus: ").append(toIndentedString(red4Plus)).append("\n");
    sb.append("    red5: ").append(toIndentedString(red5)).append("\n");
    sb.append("    yellow4Plus: ").append(toIndentedString(yellow4Plus)).append("\n");
    sb.append("    yellow5: ").append(toIndentedString(yellow5)).append("\n");
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

