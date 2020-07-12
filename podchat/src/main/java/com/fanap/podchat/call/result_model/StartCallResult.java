package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.ClientDTO;

public class StartCallResult {

	private String ca_cert;
	private String client_key;
	private String client_pem;
	private ClientDTO clientDTO;


	public String getCa_cert() {
		return ca_cert;
	}

	public void setCa_cert(String ca_cert) {
		this.ca_cert = ca_cert;
	}

	public String getClient_key() {
		return client_key;
	}

	public void setClient_key(String client_key) {
		this.client_key = client_key;
	}

	public String getClient_pem() {
		return client_pem;
	}

	public void setClient_pem(String client_pem) {
		this.client_pem = client_pem;
	}

	public ClientDTO getClientDTO() {
		return clientDTO;
	}

	public void setClientDTO(ClientDTO clientDTO) {
		this.clientDTO = clientDTO;
	}
}