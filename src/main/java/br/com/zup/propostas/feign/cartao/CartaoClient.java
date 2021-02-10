package br.com.zup.propostas.feign.cartao;

import br.com.zup.propostas.cartao.bloqueio.NovoBloqueioRequest;
import br.com.zup.propostas.cartao.carteira.NovaCarteiraRequest;
import br.com.zup.propostas.cartao.viagem.NovoAvisoViagemRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "${cartoes.url}", name = "cartao")
public interface CartaoClient {

    @GetMapping
    CartaoResponse buscarCartao(@RequestParam("idProposta") String id);

    @PostMapping("/{id}/bloqueios")
    SolicitacaoBloqueioResponse bloquearCartao(@PathVariable("id") String numero, NovoBloqueioRequest request);

    @PostMapping("/{id}/avisos")
    NovoAvisoResponse avisarViagem(@PathVariable("id") String numero, NovoAvisoViagemRequest request);

    @PostMapping("/{id}/carteiras")
    NovaCarteiraResponse incluirCarteira(@PathVariable("id") String numero, NovaCarteiraRequest request);
}
