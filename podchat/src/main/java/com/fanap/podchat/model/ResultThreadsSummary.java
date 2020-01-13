package com.fanap.podchat.model;

import com.google.gson.annotations.SerializedName;

public class ResultThreadsSummary{

	@SerializedName("id")
	private int id;

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	@Override
 	public String toString(){
		return 
			"ResultThreadsSummary{" + 
			"id = '" + id + '\'' + 
			"}";
		}
}