package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.ChatDataDTO;
import com.fanap.podchat.call.model.ClientDTO;

public class StartedCallModel {

	private String cert_file;
	private ClientDTO clientDTO;
	private ChatDataDTO chatDataDTO;
	private String callImage;
	private String callName;
	private Boolean video;
	private Boolean mute;

	public ChatDataDTO getChatDataDTO() {
		return chatDataDTO;
	}

	public void setChatDataDTO(ChatDataDTO chatDataDTO) {
		this.chatDataDTO = chatDataDTO;
	}

	public ClientDTO getClientDTO() {
		return clientDTO;
	}

	public void setClientDTO(ClientDTO clientDTO) {
		this.clientDTO = clientDTO;
	}

	public String getCert_file() {
		return cert_file;
	}

	public void setCert_file(String cert_file) {
		this.cert_file = cert_file;
	}

	public String getCallImage() {
		return callImage;
	}

	public StartedCallModel setCallImage(String callImage) {
		this.callImage = callImage;
		return this;
	}

	public String getCallName() {
		return callName;
	}

	public StartedCallModel setCallName(String callName) {
		this.callName = callName;
		return this;
	}

	public Boolean getVideo() {
		return video;
	}

	public void setVideo(Boolean video) {
		this.video = video;
	}

	public Boolean getMute() {
		return mute;
	}

	public StartedCallModel setMute(Boolean mute) {
		this.mute = mute;
		return this;
	}

}