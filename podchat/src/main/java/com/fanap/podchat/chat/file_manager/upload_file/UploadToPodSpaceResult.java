package com.fanap.podchat.chat.file_manager.upload_file;

import com.google.gson.annotations.SerializedName;

public class UploadToPodSpaceResult {

	@SerializedName("size")
	private int size;

	@SerializedName("hash")
	private String hash;

	@SerializedName("created")
	private long created;

	@SerializedName("name")
	private String name;

	@SerializedName("parentHash")
	private String parentHash;

	@SerializedName("type")
	private String type;

	public void setSize(int size){
		this.size = size;
	}

	public int getSize(){
		return size;
	}

	public void setHashCode(String hashCode){
		this.hash = hashCode;
	}

	public String getHashCode(){
		return hash;
	}

	public void setCreated(long created){
		this.created = created;
	}

	public long getCreated(){
		return created;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setParentHash(String parentHash){
		this.parentHash = parentHash;
	}

	public String getParentHash(){
		return parentHash;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	@Override
 	public String toString(){
		return 
			"Result{" + 
			"size = '" + size + '\'' + 
			",hash = '" + hash + '\'' +
			",created = '" + created + '\'' + 
			",name = '" + name + '\'' + 
			",parentHash = '" + parentHash + '\'' + 
			",type = '" + type + '\'' + 
			"}";
		}
}