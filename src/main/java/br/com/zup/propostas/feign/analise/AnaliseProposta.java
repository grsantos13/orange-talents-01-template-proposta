package br.com.zup.propostas.feign.analise;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(url = "${analise-proposta.url}", name = "analiseProposta")
public interface AnaliseProposta {

    @PostMapping("/api/solicitacao")
    AnalisePropostaResponse analisarProposta(@RequestBody @Valid AnalisePropostaRequest request);
}
