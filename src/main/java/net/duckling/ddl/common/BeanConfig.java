package net.duckling.ddl.common;

import net.duckling.common.DucklingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.duckling.vmt.api.impl.GroupService;
import net.duckling.vmt.api.impl.UserService;

@Configuration
public class BeanConfig {
    final Logger log = LoggerFactory.getLogger(BeanConfig.class);

    private String vmtUrl;

    public BeanConfig() {
    }

    public BeanConfig(DucklingProperties config) {
        vmtUrl = config.getProperty("duckling.vmt.service.address");
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

}
