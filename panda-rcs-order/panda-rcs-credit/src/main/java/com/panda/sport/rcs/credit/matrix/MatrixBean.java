package com.panda.sport.rcs.credit.matrix;

public class MatrixBean {
	
	/**
	 * 是否走矩阵
	 * 0：矩阵  2：穷举
	 */
	private Integer recType;
	
	
	private Long[][] matrixValueArray;
	
	private Integer[][] matrixStatusArray;
	
	private String statusZip;
	
	public MatrixBean(Integer recType){
		this.recType  = recType;
		this.statusZip = "";
	}


	public Integer getRecType() {
		return recType;
	}


	public void setRecType(Integer recType) {
		this.recType = recType;
	}


	public Long[][] getMatrixValueArray() {
		return matrixValueArray;
	}


	public void setMatrixValueArray(Long[][] matrixValueArray) {
		this.matrixValueArray = matrixValueArray;
	}


	public Integer[][] getMatrixStatusArray() {
		return matrixStatusArray;
	}


	public void setMatrixStatusArray(Integer[][] matrixStatusArray) {
		this.matrixStatusArray = matrixStatusArray;
	}


	public String getStatusZip() {
		return statusZip;
	}


	public void setStatusZip(String statusZip) {
		this.statusZip = statusZip;
	}

}
