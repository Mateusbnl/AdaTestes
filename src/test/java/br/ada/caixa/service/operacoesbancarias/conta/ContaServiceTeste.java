package br.ada.caixa.service.operacoesbancarias.conta;

import br.ada.caixa.entity.Conta;
import br.ada.caixa.exceptions.ValidacaoException;
import br.ada.caixa.respository.ClienteRepository;
import br.ada.caixa.respository.ContaRepository;
import br.ada.caixa.service.conta.ContaService;
import br.ada.caixa.service.operacoesbancarias.investimento.InvestimentoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ContaServiceTeste {

    @InjectMocks
    private ContaService contaService;
    @Mock
    private ContaRepository contaRepository;
    @Mock
    private ClienteRepository clienteRepository;


    @Test
    @DisplayName("Devera negar ao tentar realizar abertura de conta para cliente inexistente")
    void NegarAberturaDeContaParaClienteInexistente(){
        //given
        String expected = "Cliente nao encontrado com o CPF informado!";
        //given
        ValidacaoException actual = assertThrows(ValidacaoException.class, () -> contaService.abrirContaPoupanca("06081970418"));
        //then
        assertEquals(expected,actual.getMessage());

    }
}
