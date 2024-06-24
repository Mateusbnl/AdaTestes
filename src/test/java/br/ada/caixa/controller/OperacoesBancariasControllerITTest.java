package br.ada.caixa.controller;

import br.ada.caixa.dto.request.DepositoRequestDto;
import br.ada.caixa.dto.request.SaqueRequestDto;
import br.ada.caixa.dto.request.TransferenciaRequestDto;
import br.ada.caixa.entity.Cliente;
import br.ada.caixa.entity.Conta;
import br.ada.caixa.entity.TipoCliente;
import br.ada.caixa.entity.TipoConta;
import br.ada.caixa.enums.StatusCliente;
import br.ada.caixa.respository.ClienteRepository;
import br.ada.caixa.respository.ContaRepository;
import br.ada.caixa.service.conta.ContaService;
import br.ada.caixa.service.operacoesbancarias.deposito.DepositoService;
import br.ada.caixa.service.operacoesbancarias.investimento.InvestimentoService;
import br.ada.caixa.service.operacoesbancarias.saldo.SaldoService;
import br.ada.caixa.service.operacoesbancarias.saque.SaqueService;
import br.ada.caixa.service.operacoesbancarias.transferencia.TransferenciaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class OperacoesBancariasControllerITTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @SpyBean
    private ContaRepository contaRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    DepositoService depositoService;
    @Autowired
    SaqueService saqueService;
    @Autowired
    TransferenciaService transferenciaService;
    @Autowired
    SaldoService saldoService;
    @Autowired
    InvestimentoService investimentoService;
    @Autowired
    ContaService contaService;

    private String url;

    @BeforeEach
    void setUp() {
        //SET URL
        url = "http://localhost:" + port + "/operacoes";

        //CRIAR CLIENTES
        var cliente1 = Cliente.builder()
                .documento("123456889")
                .nome("Teste 1")
                .dataNascimento(LocalDate.now())
                .status(StatusCliente.ATIVO)
                .tipo(TipoCliente.PF)
                .createdAt(LocalDate.now())
                .build();
        var cliente2 = Cliente.builder()
                .documento("1234567891")
                .nome("Teste 2")
                .dataNascimento(LocalDate.now())
                .status(StatusCliente.ATIVO)
                .tipo(TipoCliente.PF)
                .createdAt(LocalDate.now())
                .build();
        var cliente3 = Cliente.builder()
                .documento("06081970418")
                .nome("Teste 3")
                .dataNascimento(LocalDate.now())
                .status(StatusCliente.ATIVO)
                .tipo(TipoCliente.PF)
                .createdAt(LocalDate.now())
                .build();

        clienteRepository.saveAllAndFlush(List.of(cliente1, cliente2, cliente3));

        //CRIAR CONTAS
        var contaCorrente1 = Conta.builder()
                .numero(1L)
                .saldo(BigDecimal.ZERO)
                .tipo(TipoConta.CONTA_CORRENTE)
//                .cliente(clienteRepository.findByDocumento(cliente1.getDocumento()).get())
                .cliente(cliente1)
                .createdAt(LocalDate.now())
                .build();

        var contaCorrente2 = Conta.builder()
                .numero(2L)
                .saldo(BigDecimal.ZERO)
                .tipo(TipoConta.CONTA_CORRENTE)
//                .cliente(clienteRepository.findByDocumento(cliente2.getDocumento()).get())
                .cliente(cliente2)
                .createdAt(LocalDate.now())
                .build();

        var contaCorrente3 = Conta.builder()
                .numero(3L)
                .saldo(BigDecimal.valueOf(200L))
                .tipo(TipoConta.CONTA_CORRENTE)
//                .cliente(clienteRepository.findByDocumento(cliente2.getDocumento()).get())
                .cliente(cliente3)
                .createdAt(LocalDate.now())
                .build();


        contaRepository.saveAllAndFlush(List.of(contaCorrente1, contaCorrente2, contaCorrente3));

    }

    @AfterEach
    void tearDown() {
        contaRepository.deleteAllInBatch();
        clienteRepository.deleteAllInBatch();
    }

    @Test
    void depositarTest() {
        //given
        final var valor = BigDecimal.valueOf(100.50);
        final var numeroConta = 1L;
        DepositoRequestDto depositoRequestDto =
                DepositoRequestDto.builder()
                        .numeroConta(numeroConta)
                        .valor(valor)
                        .build();
        //when
        var response = restTemplate.postForEntity(url + "/depositar", depositoRequestDto, Void.class);



        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(valor.compareTo(contaRepository.findByNumero(numeroConta).get().getSaldo())).isZero();
        assertEquals(0, valor.compareTo(contaRepository.findByNumero(numeroConta).get().getSaldo()));

        Mockito.verify(contaRepository).save(any(Conta.class));
    }

    @Test
    void sacar() {
        final var valor = BigDecimal.valueOf(100L);
        final var numeroConta = 3L;
        DecimalFormat df = new DecimalFormat("#.00");
        SaqueRequestDto saqueRequestDto = SaqueRequestDto.builder().numeroConta(numeroConta).valor(valor).build();

        //when
        var response = restTemplate.postForEntity(url + "/sacar", saqueRequestDto, Void.class);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(df.format(100), df.format(contaRepository.findByNumero(numeroConta).get().getSaldo()));

        Mockito.verify(contaRepository).save(any(Conta.class));
    }

    @Test
    void transferencia() {
        final var valor = BigDecimal.valueOf(100L);
        final var numeroContaOrigem = 3L;
        final var numeroContaDestino = 1L;
        DecimalFormat df = new DecimalFormat("#.00");
        TransferenciaRequestDto transferenciaRequestDto = TransferenciaRequestDto.builder().numeroContaOrigem(numeroContaOrigem).numeroContaDestino(numeroContaDestino).valor(valor).build();
        //when
        var response = restTemplate.postForEntity(url + "/transferir", transferenciaRequestDto, Void.class);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(df.format(100), df.format(contaRepository.findByNumero(numeroContaOrigem).get().getSaldo()));
        assertEquals(df.format(100), df.format(contaRepository.findByNumero(numeroContaDestino).get().getSaldo()));
    }

    @Test
    void consultarSaldo() {
    }

    @Test
    void investir() {
    }

    @Test
    void abrirContaPoupanca() {
    }
}