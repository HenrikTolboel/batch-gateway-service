package net.example.batchgateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("my.service")
public class MyProperties {
    private boolean enabled;

    private String name1;
    private String name2;
    private String name3;
    private String name4;
    private String name5;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(final String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(final String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(final String name3) {
        this.name3 = name3;
    }

    public String getName4() {
        return name4;
    }

    public void setName4(final String name4) {
        this.name4 = name4;
    }

    public String getName5() {
        return name5;
    }

    public void setName5(final String name5) {
        this.name5 = name5;
    }


    @Override
    public String toString() {
        return "MyProperties{" +
                "enabled=" + enabled +
                ", name1='" + name1 + '\'' +
                ", name2='" + name2 + '\'' +
                ", name3='" + name3 + '\'' +
                ", name4='" + name4 + '\'' +
                ", name5='" + name5 + '\'' +
                '}';
    }

}
