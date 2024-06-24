package br.ada.caixa.service.operacoesbancarias.deposito;

import br.ada.caixa.exceptions.ValidacaoException;
import br.ada.caixa.respository.ClienteRepository;
import br.ada.caixa.respository.ContaRepository;
import br.ada.caixa.service.conta.ContaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
@ExtendWith(MockitoExtension.class)
public class DepositoServiceTest {

    @InjectMocks
    private DepositoService depositoService;
    @Mock
    private ContaRepository contaRepository;
    @Mock
    private ClienteRepository clienteRepository;


    @Test
    @DisplayName("NegarDepositoEmContaInexistente")
    void NegarDepositoEmContaInexistente(){
        //given
        String expected = "Conta inválida!";
        //given
        ValidacaoException actual = assertThrows(ValidacaoException.class, () -> depositoService.depositar(12321L, BigDecimal.valueOf(100.00)));
        //then
        assertEquals(expected,actual.getMessage());
    }
}
