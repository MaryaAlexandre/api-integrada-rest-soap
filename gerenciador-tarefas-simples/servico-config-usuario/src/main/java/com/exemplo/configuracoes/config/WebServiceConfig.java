package com.exemplo.configuracoes.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs // Habilita o suporte a Web Services no Spring
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true); // Permite a geração automática do WSDL
        return new ServletRegistrationBean(servlet, "/ws/*"); // Mapeia o servlet para o caminho /ws
    }

    @Bean(name = "UserConfigService") // Nome que será dado ao WSDL gerado (ex: UserConfigService.wsdl)
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema userConfigSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("UserConfigServicePort"); // Nome do portType no WSDL
        wsdl11Definition.setLocationUri("/ws"); // Base URI para as operações do serviço
        wsdl11Definition.setTargetNamespace("http://example.com/configuracoes"); // Namespace alvo (igual ao XSD e Service)
        wsdl11Definition.setSchema(userConfigSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema userConfigSchema() {
        // Carrega o schema XSD do diretório resources/schemas/
        return new SimpleXsdSchema(new ClassPathResource("schemas/userConfig.xsd"));
    }
}