package com.exemplo.configuracoes.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserConfig", propOrder = {
    "userId",
    "corPreferencial"
})
@XmlRootElement(namespace = "http://example.com/configuracoes") // Importante: namespace igual ao do XSD e Service
public class UserConfig {
    @XmlElement(required = true)
    protected String userId;
    @XmlElement(required = true)
    protected String corPreferencial;

    public UserConfig() {}

    public UserConfig(String userId, String corPreferencial) {
        this.userId = userId;
        this.corPreferencial = corPreferencial;
    }

    // --- Getters e Setters ---
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getCorPreferencial() { return corPreferencial; }
    public void setCorPreferencial(String corPreferencial) { this.corPreferencial = corPreferencial; }
}