package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// Iniciando a classe de serviço para o Restaurante
@Service
@Transactional
public class RestauranteService {

    // injetando o repositório de Restaurante
    @Autowired
    private RestauranteRepository restauranteRepository;

    /*
     Cadastrando novo restaurante e lançando exceção caso o nome já esteja cadastrado
     através do método findByNome do repositório de Restaurante
     e retornando o restaurante cadastrado através do método save do repositório de Restaurante
     usando o metodo illegalArgumentException para lançar
     a exceção caso o nome já esteja cadastrado
     */
    public Restaurante cadastrar(Restaurante restaurante) {
        // validação de nome único
        if (restauranteRepository.findByNome(restaurante.getNome()).isPresent()) {
            throw new IllegalArgumentException("Restaurante já cadastrado: " + restaurante.getNome());
        }

        // validação de restaurante
        validarDadosRestaurante(restaurante);

        // definindo o status do restaurante como ativo
        restaurante.setAtivo(true);

        return restauranteRepository.save(restaurante);
    }

    /*
    Buscar o restaurante por Id
    */
    @Transactional(readOnly = true)
    public Optional<Restaurante> buscarPorId(Long id) {
        return restauranteRepository.findById(id);
    }

    /*
    Listar os restaurantes ativos
    */
    @Transactional(readOnly = true)
    public List<Restaurante> listarAtivos() {
        return restauranteRepository.findByAtivoTrue();
    }

    /*
    Buscar restaurantes ativos por categoria
    */
    @Transactional(readOnly = true)
    public List<Restaurante> buscarPorCategoria(String categoria) {
        return restauranteRepository.findByCategoriaAndAtivoTrue(categoria);
    }

    /*
    Atualizar os dados do restaurante
    */
    public Restaurante atualizar(Long id, Restaurante restauranteAtualizado) {
        // buscando o restaurante pelo id e lançando exceção caso não seja encontrado
        Restaurante restaurante = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));

        // Verificar se o nome não está sendo usado por outro restaurante (se mudou)
        if (!restaurante.getNome().equals(restauranteAtualizado.getNome()) &&
                restauranteRepository.findByNome(restauranteAtualizado.getNome()).isPresent()) {
            // lançando exceção caso o nome já esteja cadastrado
            throw new IllegalArgumentException("Nome já cadastrado: " + restauranteAtualizado.getNome());
        }

        // Atualizando os dados do restaurante com os dados do restauranteAtualizado
        restaurante.setNome(restauranteAtualizado.getNome());
        restaurante.setCategoria(restauranteAtualizado.getCategoria());
        restaurante.setEndereco(restauranteAtualizado.getEndereco());
        restaurante.setTelefone(restauranteAtualizado.getTelefone());
        restaurante.setTaxaEntrega(restauranteAtualizado.getTaxaEntrega());

        // retornando o restaurante atualizado através do método save do repositório de Restaurante
        return restauranteRepository.save(restaurante);
    }

    /* inativação do restaurante através do metodo void */
    public void inativar(Long id) {
        // restaurante acessando restaurante e buscando pelo id
        Restaurante restaurante = buscarPorId(id)
                // através do método orElseThrow lançando exceção caso o restaurante não seja encontrado
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));

        // definindo o status do restaurante como inativo
        restaurante.setAtivo(false);

        // salvando no banco de dados
        restauranteRepository.save(restaurante);
    }

    /*
        Validação das regras de Negocio do Restaurante,
        como nome e taxa de entrega
    */
    private void validarDadosRestaurante(Restaurante restaurante) {
        // verificando se o nome do restaurante está preenchido
        if (restaurante.getNome() == null || restaurante.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        // verificando se a taxa de entrega não é negativa
        if (restaurante.getTaxaEntrega() != null &&
                // verificando se a taxa de entrega é menor que zero
                restaurante.getTaxaEntrega().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Taxa de entrega não pode ser negativa");
        }
    }
}
