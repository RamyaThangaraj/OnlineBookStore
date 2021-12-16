package com.sampleproject.obs.hcs.dockercomposereader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

public final class DockerComposeReader {
	
	private static final Logger logger = LoggerFactory.getLogger(DockerComposeReader.class);
	
//	@Autowired
//	private static MongoDBLoggerService mongoDBLoggerService;
	@Autowired
	private static Environment env;

	public static DockerCompose parse(String dockerFileLocation) throws Exception {
		InputStream inputStream = null;

		logger.info("Loading app net configuration from docker-compose.yml");

		File configFile = new File(dockerFileLocation);
		if (configFile.exists()) {
			logger.info("Found app net configuration in " + dockerFileLocation);
			inputStream = new FileInputStream(configFile.getCanonicalPath());
		}
		if (Objects.nonNull(inputStream)) {
			Representer representer = new Representer();
			representer.getPropertyUtils().setSkipMissingProperties(true);
			Yaml yaml = new Yaml(new Constructor(DockerCompose.class), representer);
			return yaml.load(inputStream);
		} else {
//			mongoDBLoggerService.createLogger(env.getProperty("docker.file.not.found") + dockerFileLocation,
//					ServiceType.HCS,dockerFileLocation,"onload","dockercompose","400");
			throw new Exception(env.getProperty("docker.file.not.found") + dockerFileLocation);
		}
	}

	public static DockerCompose parse() throws Exception {
		return parse("./config/docker-compose.yml");
	}

}
