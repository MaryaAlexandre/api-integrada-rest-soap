package com.exemplo.configuracoes.service;
import com.exemplo.configuracoes.model.UserConfig;
import org.springframework.stereotype.Service;
import jakarta.jws.WebService;
import java.util.HashMap;
import java.util.Map;

@Service
@WebService(endpointInterface = "com.exemplo.configuracoes.service.UserConfigService",
            targetNamespace = "http://example.com/configuracoes",
            serviceName = "UserConfigService",
            portName = "UserConfigServicePort")
public class UserConfigServiceImpl implements UserConfigService {

    private Map<String, UserConfig> userConfigs = new HashMap<>();

    public UserConfigServiceImpl() {
        userConfigs.put("user1", new UserConfig("user1", "azul"));
        userConfigs.put("user2", new UserConfig("user2", "verde"));
    }

    @Override
    public UserConfig getCorPreferencial(String userId) {
        System.out.println("SOAP: Recebida requisição getCorPreferencial para userId: " + userId);
        return userConfigs.getOrDefault(userId, new UserConfig(userId, "padrao"));
    }

    @Override
    public String setCorPreferencial(UserConfig config) {
        System.out.println("SOAP: Recebida requisição setCorPreferencial para userId: " + config.getUserId() + " com cor: " + config.getCorPreferencial());
        userConfigs.put(config.getUserId(), config);
        return "Cor preferencial para " + config.getUserId() + " definida como " + config.getCorPreferencial() + ".";
    }
}