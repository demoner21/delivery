package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Iniciando a classe de serviço para o Cliente
@Service
public class ClienteService {
    
    // injetando o repositório de Cliente
    @Autowired
    private ClienteRepository clienteRepository;

    /*
     Cadastrando novo cliente e lançando exceção caso o email já esteja cadastrado
     atrvés do método existsByEmail do repositório de Cliente
     e retornando o cliente cadastrado através do método save do repositório de Cliente
     usando o metodo illegalArgumentException para lançar 
     a exceção caso o email já esteja cadastrado
     */
    public Cliente cadastrarCliente(Cliente cliente) {
        // validação de email unica
        if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado." + cliente.getEmail());
        }

        // validação de cliente
        validarCliente(cliente);

        // definindo o status do cliente como ativo
        cliente.setAtivo(true);

        return clienteRepository.save(cliente);
    }

    /*
    Buscar os clientes por Id
    */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    /* Busca pelo email do cliente */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarClientePorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    /*
    Buscar clientes por nome
    */
    @Transactional(readOnly = true)
    public List<Cliente> buscarClientesPorNome(String nome) {   
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    /*
    Listar todos os clientes ativos
    */
    @Transactional(readOnly = true)
    public List<Cliente> listarAtivos() {
        return clienteRepository.findByAtivoTrue();
    }

    /*
    Atualizar os dados do cliente
    */

    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        // validação de cliente
        validarCliente(clienteAtualizado);
        Cliente cliente = clienteRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com o ID: " + id));
        
        // Verificar se o email não esta sendo usado por outro cliente
        if (!cliente.getEmail().equals(clienteAtualizado.getEmail()) && 
            // verificando se o email já está cadastrado no banco de dados
            clienteRepository.findByEmail(clienteAtualizado.getEmail()).isPresent()) {
                // lançando exceção caso o email já esteja cadastrado
                throw new IllegalArgumentException("Email já cadastrado: " + clienteAtualizado.getEmail());
        }

        // Atualizando os dados do cliente com os dados do clienteAtualizado
        cliente.setNome(clienteAtualizado.getNome());
        cliente.setEmail(clienteAtualizado.getEmail());
        cliente.setTelefone(clienteAtualizado.getTelefone());
        cliente.setEndereco(clienteAtualizado.getEndereco());

        // retornando o cliente atualizado através do método save do repositório de Cliente
        return clienteRepository.save(cliente);
    }

    /* inativaçáo do cliente através do metodo void */
    public void inativarCliente(Long id) {
        // cliente acessando cliente e buscando pelo id
        Cliente cliente = clienteRepository.findById(id)
                // através do método orElseThrow lançando exceção caso o cliente não seja encontrado
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com o ID: " + id));
        // definindo o status do cliente como inativo 
        cliente.setAtivo(false);
        // salvando no banco de dados
        clienteRepository.save(cliente);
    }

    /*
        Validação das regras de Negocio do Cliente,
        como nome, email, telefone e endereço 
    */

    private void validarCliente(Cliente cliente) {
        // uso do trim() para remover espaços em branco no início e no final da string
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório.");   
        }
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email do cliente é obrigatório.");
        }
        if (cliente.getTelefone() == null || cliente.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone do cliente é obrigatório.");
        }
        if (cliente.getEndereco() == null || cliente.getEndereco().trim().isEmpty()) {
            throw new IllegalArgumentException("Endereço do cliente é obrigatório.");
        }
    }
}
