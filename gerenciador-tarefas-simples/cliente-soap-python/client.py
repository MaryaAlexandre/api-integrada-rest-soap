from zeep import Client, Settings
import logging


logging.basicConfig(level=logging.DEBUG)
logging.getLogger('zeep.transports').setLevel(logging.DEBUG)


wsdl_url = 'http://localhost:8081/ws/UserConfigService?wsdl'

print(f"--- Tentando conectar ao WSDL em: {wsdl_url} ---")

try:
    settings = Settings(strict=False, xml_huge_tree=True)
    client = Client(wsdl=wsdl_url, settings=settings)

    print("\n--- Conexão com o Serviço SOAP estabelecida ---")
    print("Operações disponíveis (métodos gerados a partir do WSDL):")
    print(client.service) # Mostra as operações do serviço

    # --- Chamando a operação getCorPreferencial ---
    print("\n--- Testando 'getCorPreferencial' para user1 ---")
    user_id_get = "user1"
    try:
        # O Zeep usa o WSDL para mapear os parâmetros da operação
        config_retornada = client.service.getCorPreferencial(userId=user_id_get)
        print(f"Configuração para '{user_id_get}':")
        print(f"  ID do Usuário: {config_retornada.userId}")
        print(f"  Cor Preferencial: {config_retornada.corPreferencial}")
    except Exception as e:
        print(f"Erro ao buscar configuração para '{user_id_get}': {e}")

    # --- Chamando a operação setCorPreferencial ---
    print("\n--- Testando 'setCorPreferencial' para user3 ---")
    user_id_set = "user3"
    nova_cor = "roxo"
    UserConfigType = client.get_type('ns0:UserConfig')
    nova_config = UserConfigType(userId=user_id_set, corPreferencial=nova_cor)

    try:

        resposta_set = client.service.setCorPreferencial(userConfig=nova_config)
        print(f"Resposta ao definir configuração para '{user_id_set}': {resposta_set}")
    except Exception as e:
        print(f"Erro ao definir configuração para '{user_id_set}': {e}")

    # --- Verificando a nova configuração ---
    print(f"\n--- Verificando 'getCorPreferencial' para {user_id_set} (após set) ---")
    try:
        config_retornada_nova = client.service.getCorPreferencial(userId=user_id_set)
        print(f"Configuração recém-definida para '{user_id_set}':")
        print(f"  ID do Usuário: {config_retornada_nova.userId}")
        print(f"  Cor Preferencial: {config_retornada_nova.corPreferencial}")
    except Exception as e:
        print(f"Erro ao buscar nova configuração para '{user_id_set}': {e}")

except Exception as e:
    print(f"\n--- ERRO FATAL: Não foi possível conectar ao serviço SOAP ou processar WSDL ---")
    print(f"Verifique se o 'servico-config-usuario' está rodando na porta 8081.")
    print(f"Detalhes do erro: {e}")
