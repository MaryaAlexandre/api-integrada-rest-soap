const API_GATEWAY_URL = 'http://localhost:8080'; // Gateway rodará na porta 8080

document.addEventListener('DOMContentLoaded', () => {
    carregarTarefas();
    showSection('criar-tarefa');
});

function showSection(id) {
    document.querySelectorAll('section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById(id).classList.add('active');
    if (id === 'listar-tarefas') {
        carregarTarefas();
    }
}

document.getElementById('form-criar-tarefa').addEventListener('submit', async (e) => {
    e.preventDefault();
    const titulo = document.getElementById('titulo').value;
    const descricao = document.getElementById('descricao').value;
    const novaTarefa = { titulo, descricao };

    try {
        const response = await fetch(`${API_GATEWAY_URL}/api/tarefas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(novaTarefa),
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        document.getElementById('titulo').value = '';
        document.getElementById('descricao').value = '';
        alert('Tarefa criada com sucesso!');
        carregarTarefas();
        showSection('listar-tarefas');
    } catch (error) {
        console.error('Erro ao criar tarefa:', error);
        alert('Erro ao criar tarefa. Verifique o console.');
    }
});

async function carregarTarefas() {
    const listaTarefas = document.getElementById('lista-tarefas');
    listaTarefas.innerHTML = '';
    document.getElementById('sem-tarefas-msg').style.display = 'none';

    try {
        const response = await fetch(`${API_GATEWAY_URL}/api/tarefas`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const tarefas = await response.json();

        if (tarefas.length === 0) {
            document.getElementById('sem-tarefas-msg').style.display = 'block';
            return;
        }

        tarefas.forEach(tarefa => {
            const li = document.createElement('li');
            const statusClass = tarefa.status === 'CONCLUIDA' ? 'status-concluida' : 'status-pendente';
            li.innerHTML = `<span><strong>${tarefa.titulo}</strong>: ${tarefa.descricao} <span class="${statusClass}">(${tarefa.status})</span></span>`;

            if (tarefa._links && tarefa._links.marcar_como_concluida) {
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

// ESTA FUNÇÃO CONTÉM A CORREÇÃO DO BUG
async function marcarTarefaConcluida(urlHateoas) {
    try {
        // A correção é adicionar o API_GATEWAY_URL, pois o link do HATEOAS é relativo
        const response = await fetch(`${API_GATEWAY_URL}${urlHateoas}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        alert('Tarefa marcada como concluída!');
        carregarTarefas();
    } catch (error) {
        console.error('Erro ao marcar tarefa como concluída:', error);
        alert('Erro ao marcar tarefa como concluída. Verifique o console.');
    }
}

async function getCorPreferencialWeb() {
    const userId = document.getElementById('config-user-id').value;
    const resultadoParagrafo = document.getElementById('cor-preferencial-resultado');
    resultadoParagrafo.textContent = 'Buscando...';
    const soapEndpoint = `${API_GATEWAY_URL}/soap/ws/UserConfigService`;
    const soapRequest = `... seu XML SOAP aqui ...`; // O XML SOAP continua o mesmo

    try {
        const response = await fetch(soapEndpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'text/xml; charset=utf-8' },
            body: soapRequest,
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const textResponse = await response.text();
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(textResponse, "application/xml");
        const corElement = xmlDoc.querySelector("corPreferencial");
        const cor = corElement ? corElement.textContent : 'Não encontrada';
        resultadoParagrafo.textContent = `Cor Preferencial para ${userId}: ${cor}`;
    } catch (error) {
        console.error('Erro ao obter cor preferencial:', error);
        resultadoParagrafo.textContent = 'Erro ao obter cor preferencial.';
    }
}

document.getElementById('form-config-usuario').addEventListener('submit', async (e) => {
    e.preventDefault();
    const userId = document.getElementById('config-user-id').value;
    const cor = document.getElementById('cor-preferencial').value;
    const resultadoParagrafo = document.getElementById('cor-preferencial-resultado');
    resultadoParagrafo.textContent = 'Definindo...';
    const soapEndpoint = `${API_GATEWAY_URL}/soap/ws/UserConfigService`;
    const soapRequest = `... seu XML SOAP aqui ...`; // O XML SOAP continua o mesmo

    try {
        const response = await fetch(soapEndpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'text/xml; charset=utf-8' },
            body: soapRequest,
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const textResponse = await response.text();
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(textResponse, "application/xml");
        const returnElement = xmlDoc.querySelector("return");
        const statusMsg = returnElement ? returnElement.textContent : "Resposta recebida.";
        resultadoParagrafo.textContent = `Status: ${statusMsg}`;
        alert('Cor preferencial definida com sucesso (via Gateway)!');
    } catch (error) {
        console.error('Erro ao definir cor preferencial:', error);
        resultadoParagrafo.textContent = 'Erro ao definir cor preferencial.';
    }
});