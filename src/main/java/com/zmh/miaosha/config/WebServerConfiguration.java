package com.zmh.miaosha.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

//TODO 当spring容器中没有TomcatEmbeddedServletContainerFactory这个bean时,会加载此bean
@Component
public class WebServerConfiguration implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory configurableWebServerFactory) {
        // 使用对应工厂类提供给我们的接口 定制我们的Tomcat connector
        ((TomcatServletWebServerFactory)configurableWebServerFactory).addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                final Http11NioProtocol protocolHandler = (Http11NioProtocol) connector.getProtocolHandler();
                //定制化keepalivetimeou,设置30秒内没有请求则服务器端断开keepalive连接
                protocolHandler.setKeepAliveTimeout(30000);
                //当客户端发送超过10000个请求则自动断开keepalive连接
                protocolHandler.setMaxKeepAliveRequests(10000);
                // 还可以设置最大连接数
            }
        });
    }
}
