package com.exemplo.configuracoes.service;

import com.exemplo.configuracoes.model.UserConfig;
import jakarta.jws.WebService;

@WebService(targetNamespace = "http://example.com/configuracoes")
public interface UserConfigService {

    UserConfig getCorPreferencial(String userId);

    String setCorPreferencial(UserConfig userConfig);
}