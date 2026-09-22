package com.hab.emmaus.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.cors")
public class AllowedOriginsConfigProp {
    List<String> origins;
}
