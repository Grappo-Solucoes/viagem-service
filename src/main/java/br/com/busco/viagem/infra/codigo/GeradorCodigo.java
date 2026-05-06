package br.com.busco.viagem.infra.codigo;

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
