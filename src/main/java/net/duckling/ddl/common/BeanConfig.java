package net.duckling.ddl.common;

import net.duckling.common.DucklingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.duckling.vmt.api.impl.GroupService;
import net.duckling.vmt.api.impl.UserService;
import net.duckling.falcon.api.idg.IIDGeneratorService;
import net.duckling.falcon.api.idg.impl.BasicIDGenerator;
import net.duckling.falcon.api.idg.impl.IDGeneratorService;

@Configuration
public class BeanConfig {
    final Logger log = LoggerFactory.getLogger(BeanConfig.class);

    private String vmtUrl;
    private String redisHost;
    private int redisPort;

    public BeanConfig() {
    }

    public BeanConfig(DucklingProperties config) {
        vmtUrl = config.getProperty("duckling.vmt.service.address");
        redisHost = config.getProperty("duckling.redis.server");
        redisPort = config.getInt("duckling.redis.port", 6379);
    }

    @Bean
    public GroupService groupService() {
        if (vmtUrl == null || "null".equals(vmtUrl)) {
            return null;
        } else {
            return new GroupService(vmtUrl);
        }
    }

    @Bean
    public UserService vmtUserService() {
        if (vmtUrl == null || "null".equals(vmtUrl)) {
            return null;
        } else {
            return new UserService(vmtUrl);
        }
    }

    @Bean(destroyMethod = "close")
    public IIDGeneratorService idGeneratorService() {
        if (redisHost == null || "null".equals(redisHost)) {
            return new BasicIDGenerator();
        } else {
            return new IDGeneratorService(redisHost, redisPort);
        }
    }

}
