package com.sampleproject.obs.hcs.dockercomposereader;

import java.util.Map;

public final class DockerCompose {
	
	private String version;
	private Map<String, DockerService> services;

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Map<String, DockerService> getServices() {
		return this.services;
	}

	public void setServices(Map<String, DockerService> services) {
		this.services = services;
	}

	public String getNameForId(String appId) {
		String name = "not found";
		for (Map.Entry<String, DockerService> service : this.services.entrySet()) {
			DockerService dockerService = service.getValue();
			if (dockerService.getEnvironment() != null) {
				if (dockerService.getEnvironment().containsKey("APP_ID")) {
					if (dockerService.getEnvironment().get("APP_ID").contentEquals(appId)) {
						return dockerService.getContainer_name();
					}
				}
			}
		}
		return name;
	}

	public String getPublicKeyForId(String appId) {
		String key = "not found";
		for (Map.Entry<String, DockerService> service : this.services.entrySet()) {
			DockerService dockerService = service.getValue();
			if (dockerService.getEnvironment() != null) {
				if (dockerService.getEnvironment().containsKey("PUBKEY")) {
					if (dockerService.getEnvironment().get("APP_ID").contentEquals(appId)) {
						return dockerService.getEnvironment().get("PUBKEY");
					}
				}
			}
		}
		return key;
	}

	public int getPortForId(String appId) {
		int port = 0;
		for (Map.Entry<String, DockerService> service : this.services.entrySet()) {
			DockerService dockerService = service.getValue();
			if (dockerService.getEnvironment() != null) {
				if (dockerService.getEnvironment().get("APP_ID").contentEquals(appId)) {
					port = dockerService.getPortAsInteger();
				}
			}
		}
		return port;
	}

}
