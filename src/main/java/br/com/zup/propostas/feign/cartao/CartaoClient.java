package br.com.zup.propostas.feign.cartao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "${cartoes.url}", name = "cartao")
public interface CartaoClient {

    @GetMapping
    CartaoResponse buscarCartao(@RequestParam("idProposta") String id);
}
