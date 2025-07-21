const API_GATEWAY_URL = 'http://localhost:8080'; // Gateway rodará na porta 8080

document.addEventListener('DOMContentLoaded', () => {
    carregarTarefas();
    showSection('criar-tarefa'); // Exibe a seção de criar tarefa por padrão
});

// Função para alternar seções na página
function showSection(id) {
    document.querySelectorAll('section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById(id).classList.add('active');

    if (id === 'listar-tarefas') {
        carregarTarefas(); // Recarrega tarefas sempre que a seção de lista é ativada
    }
}

// --- Funções para o Serviço de Tarefas (REST via Gateway) ---
document.getElementById('form-criar-tarefa').addEventListener('submit', async (e) => {
    e.preventDefault();
    const titulo = document.getElementById('titulo').value;
    const descricao = document.getElementById('descricao').value;

    const novaTarefa = { titulo, descricao };

    try {
        const response = await fetch(`${API_GATEWAY_URL}/api/tarefas`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(novaTarefa),
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        document.getElementById('titulo').value = '';
        document.getElementById('descricao').value = '';
        alert('Tarefa criada com sucesso!');
        carregarTarefas(); // Recarrega a lista de tarefas
        showSection('listar-tarefas'); // Volta para a lista
    } catch (error) {
        console.error('Erro ao criar tarefa:', error);
        alert('Erro ao criar tarefa. Verifique o console.');
    }
});

async function carregarTarefas() {
    const listaTarefas = document.getElementById('lista-tarefas');
    listaTarefas.innerHTML = ''; // Limpa a lista existente
    document.getElementById('sem-tarefas-msg').style.display = 'none';

    try {
        const response = await fetch(`${API_GATEWAY_URL}/api/tarefas`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const tarefas = await response.json();

        if (tarefas.length === 0) {
            document.getElementById('sem-tarefas-msg').style.display = 'block';
            return;
        }

        tarefas.forEach(tarefa => {
            const li = document.createElement('li');
            const statusClass = tarefa.status === 'CONCLUIDA' ? 'status-concluida' : 'status-pendente';
            li.innerHTML = `
                <span><strong>${tarefa.titulo}</strong>: ${tarefa.descricao}
                <span class="${statusClass}">(${tarefa.status})</span></span>
            `;

            // HATEOAS: Botão para marcar como concluída, se houver o link e não estiver concluída
            if (tarefa._links && tarefa._links.marcar_como_concluida && tarefa.status !== 'CONCLUIDA') {
                const btnConcluir = document.createElement('button');
                btnConcluir.textContent = 'Marcar Concluída';
                btnConcluir.onclick = () => marcarTarefaConcluida(tarefa._links.marcar_como_concluida.href);
                li.appendChild(btnConcluir);
            }
            listaTarefas.appendChild(li);
        });
    } catch (error) {
        console.error('Erro ao carregar tarefas:', error);
        alert('Erro ao carregar tarefas. Verifique o console.');
    }
}

async function marcarTarefaConcluida(urlHateoas) {
    try {
        const response = await fetch(urlHateoas, {
            method: 'POST', // Método indicado pelo HATEOAS
            headers: {
                'Content-Type': 'application/json'
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        alert('Tarefa marcada como concluída!');
        carregarTarefas(); // Recarrega a lista para refletir a mudança
    } catch (error) {
        console.error('Erro ao marcar tarefa como concluída:', error);
        alert('Erro ao marcar tarefa como concluída. Verifique o console.');
    }
}


// --- Funções para o Serviço de Configurações do Usuário (SOAP via Gateway) ---
// NOTA: Acessar SOAP diretamente de um navegador é complexo devido ao XML e CORS.
// Aqui, o Gateway está atuando como um proxy que redireciona a requisição (mas o cliente web ainda precisa enviar o XML SOAP)
// Para simplificar para o cliente web, esta parte é mais demonstrativa do que funcionalmente "completa" sem transformações complexas no Gateway.
// O cliente Python é quem fará a integração SOAP robusta.

// Função para obter a cor preferencial (via Gateway que repassa para o SOAP)
async function getCorPreferencialWeb() {
    const userId = document.getElementById('config-user-id').value;
    const resultadoParagrafo = document.getElementById('cor-preferencial-resultado');
    resultadoParagrafo.textContent = 'Buscando...';

    // O cliente web precisa construir o XML SOAP para o Gateway, que o Gateway repassará.
    // A URL completa do endpoint SOAP via Gateway é http://localhost:8080/soap/configuracoes/ws/UserConfigService
    const soapEndpoint = `${API_GATEWAY_URL}/soap/configuracoes/ws/UserConfigService`;

    // Construindo o corpo da requisição SOAP manualmente (para simplicidade)
    const soapRequest = `<?xml version="1.0" encoding="utf-8"?>
        <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
            <soap:Body>
                <ns2:getCorPreferencial xmlns:ns2="http://example.com/configuracoes">
                    <userId>${userId}</userId>
                </ns2:getCorPreferencial>
            </soap:Body>
        </soap:Envelope>`;

    try {
        const response = await fetch(soapEndpoint, {
            method: 'POST', // Requisições SOAP são POST
            headers: {
                'Content-Type': 'text/xml; charset=utf-8', // Tipo de conteúdo para SOAP
                'SOAPAction': '""' // SOAPAction pode ser vazio ou ter o nome da operação
            },
            body: soapRequest,
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const textResponse = await response.text();
        console.log("Resposta SOAP bruta do Gateway (GET):", textResponse);

        // Parsing simples do XML para extrair a cor
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(textResponse, "application/xml");
        const corElement = xmlDoc.querySelector("return corPreferencial"); // Navegue pela estrutura de resposta SOAP
        const cor = corElement ? corElement.textContent : 'Não encontrada';

        resultadoParagrafo.textContent = `Cor Preferencial para ${userId}: ${cor}`;

    } catch (error) {
        console.error('Erro ao obter cor preferencial:', error);
        resultadoParagrafo.textContent = 'Erro ao obter cor preferencial.';
    }
}

// Função para definir a cor preferencial (via Gateway que repassa para o SOAP)
document.getElementById('form-config-usuario').addEventListener('submit', async (e) => {
    e.preventDefault();
    const userId = document.getElementById('config-user-id').value;
    const cor = document.getElementById('cor-preferencial').value;
    const resultadoParagrafo = document.getElementById('cor-preferencial-resultado');
    resultadoParagrafo.textContent = 'Definindo...';

    const soapEndpoint = `${API_GATEWAY_URL}/soap/configuracoes/ws/UserConfigService`;

    // Construindo o corpo da requisição SOAP manualmente
    const soapRequest = `<?xml version="1.0" encoding="utf-8"?>
        <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
            <soap:Body>
                <ns2:setCorPreferencial xmlns:ns2="http://example.com/configuracoes">
                    <userConfig>
                        <userId>${userId}</userId>
                        <corPreferencial>${cor}</corPreferencial>
                    </userConfig>
                </ns2:setCorPreferencial>
            </soap:Body>
        </soap:Envelope>`;

    try {
        const response = await fetch(soapEndpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'text/xml; charset=utf-8',
                'SOAPAction': '""'
            },
            body: soapRequest,
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const textResponse = await response.text();
        console.log("Resposta SOAP bruta do Gateway (SET):", textResponse);
        resultadoParagrafo.textContent = `Resposta: ${textResponse}`;
        alert('Cor preferencial definida com sucesso (via Gateway)!');
        getCorPreferencialWeb(); // Atualiza a cor mostrada
    } catch (error) {
        console.error('Erro ao definir cor preferencial:', error);
        resultadoParagrafo.textContent = 'Erro ao definir cor preferencial.';
    }
});