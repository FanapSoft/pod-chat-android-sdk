package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.ClientDTO;

public class StartCallResult {

	private String cert_file;
	private ClientDTO clientDTO;

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
}