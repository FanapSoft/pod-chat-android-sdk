package com.fanap.podchat.model;

import java.util.Map;

public class ResultNotSeenDuration{

	private Map<String,Long> idNotSeenPair;

	private Map<String, Long> getIdNotSeenPair() {
		return idNotSeenPair;
	}

	public void setIdNotSeenPair(Map<String, Long> idNotSeenPair) {
		this.idNotSeenPair = idNotSeenPair;
	}


	@Override
	public String toString() {
		return "ResultNotSeenDuration{" +
				"idNotSeenPair=" + idNotSeenPair +
				'}';
	}
}
