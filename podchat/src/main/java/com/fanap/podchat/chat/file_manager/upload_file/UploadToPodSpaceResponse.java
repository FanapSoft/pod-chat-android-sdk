package com.fanap.podchat.chat.file_manager.upload_file;

import com.google.gson.annotations.SerializedName;

public class UploadToPodSpaceResponse{

	@SerializedName("result")
	private UploadToPodSpaceResult uploadToPodSpaceResult;

	@SerializedName("referenceNumber")
	private String referenceNumber;

	@SerializedName("count")
	private int count;

	@SerializedName("errorCode")
	private int errorCode;

	@SerializedName("hasError")
	private boolean hasError;

	@SerializedName("ott")
	private String ott;

	@SerializedName("message")
    private String message;

	public void setUploadToPodSpaceResult(UploadToPodSpaceResult uploadToPodSpaceResult){
		this.uploadToPodSpaceResult = uploadToPodSpaceResult;
	}

	public UploadToPodSpaceResult getUploadToPodSpaceResult(){
		return uploadToPodSpaceResult;
	}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReferenceNumber(String referenceNumber){
		this.referenceNumber = referenceNumber;
	}

	public String getReferenceNumber(){
		return referenceNumber;
	}

	public void setCount(int count){
		this.count = count;
	}

	public int getCount(){
		return count;
	}

	public void setErrorCode(int errorCode){
		this.errorCode = errorCode;
	}

	public int getErrorCode(){
		return errorCode;
	}

	public void setHasError(boolean hasError){
		this.hasError = hasError;
	}

	public boolean isHasError(){
		return hasError;
	}

	public void setOtt(String ott){
		this.ott = ott;
	}

	public String getOtt(){
		return ott;
	}

	@Override
 	public String toString(){
		return 
			"UploadToPodSpaceResponse{" + 
			"result = '" + uploadToPodSpaceResult + '\'' +
			",referenceNumber = '" + referenceNumber + '\'' + 
			",count = '" + count + '\'' + 
			",errorCode = '" + errorCode + '\'' + 
			",hasError = '" + hasError + '\'' + 
			",ott = '" + ott + '\'' + 
			"}";
		}
}