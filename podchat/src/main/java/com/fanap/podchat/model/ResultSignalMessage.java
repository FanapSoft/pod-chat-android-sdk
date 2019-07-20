package com.fanap.podchat.model;

public class ResultSignalMessage{

	private String ssoId;
	private int coreUserId;
	private int smt;
	private String user;
	private int userId;

	public void setSsoId(String ssoId){
		this.ssoId = ssoId;
	}

	public String getSsoId(){
		return ssoId;
	}

	public void setCoreUserId(int coreUserId){
		this.coreUserId = coreUserId;
	}

	public int getCoreUserId(){
		return coreUserId;
	}

	public void setSmt(int smt){
		this.smt = smt;
	}

	public int getSmt(){
		return smt;
	}

	public void setUser(String user){
		this.user = user;
	}

	public String getUser(){
		return user;
	}

	public void setUserId(int userId){
		this.userId = userId;
	}

	public int getUserId(){
		return userId;
	}

	@Override
 	public String toString(){
		return 
			"ResultSignalMessage{" + 
			"ssoId = '" + ssoId + '\'' + 
			",coreUserId = '" + coreUserId + '\'' + 
			",smt = '" + smt + '\'' + 
			",user = '" + user + '\'' + 
			",userId = '" + userId + '\'' + 
			"}";
		}
}
