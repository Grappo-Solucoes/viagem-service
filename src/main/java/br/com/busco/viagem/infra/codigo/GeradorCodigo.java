package br.com.busco.viagem.infra.codigo;

import br.com.busco.viagem.sk.ObterUltimoCodigo;
import br.com.busco.viagem.sk.vo.Codigo;

public class GeradorCodigo {
    private final ObterUltimoCodigo repo;

    private GeradorCodigo(ObterUltimoCodigo repo) {
        this.repo = repo;
    }

    public static GeradorCodigo by(ObterUltimoCodigo repo) {
        return new GeradorCodigo(repo);
    }

    public synchronized Codigo gerar() {
        int ultimoCodigo = repo.obterUltimoCodigo().orElse(0);
        int proximoCodigo = ultimoCodigo + 1;

        return Codigo.of(proximoCodigo);
    }
}
