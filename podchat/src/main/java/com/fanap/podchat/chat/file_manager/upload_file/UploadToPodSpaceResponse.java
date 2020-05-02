package com.fanap.podchat.chat.file_manager.upload_file;

import com.google.gson.annotations.SerializedName;

public class UploadToPodSpaceResponse{

	@SerializedName("size")
	private int size;

	@SerializedName("hashCode")
	private String hashCode;

	@SerializedName("created")
	private String created;

	@SerializedName("name")
	private String name;

	@SerializedName("description")
	private String description;

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
		this.hashCode = hashCode;
	}

	public String getHashCode(){
		return hashCode;
	}

	public void setCreated(String created){
		this.created = created;
	}

	public String getCreated(){
		return created;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
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
			"UploadToPodSpaceResponse{" + 
			"size = '" + size + '\'' + 
			",hashCode = '" + hashCode + '\'' + 
			",created = '" + created + '\'' + 
			",name = '" + name + '\'' + 
			",description = '" + description + '\'' + 
			",parentHash = '" + parentHash + '\'' + 
			",type = '" + type + '\'' + 
			"}";
		}
}