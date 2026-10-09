package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ddd.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/**
 * Snapshot do passageiro esperado na viagem. O cadastro é dono dos dados;
 * aqui guardamos só o necessário para a execução (id + snapshot + ponto + ativo).
 * Viagens já iniciadas/finalizadas congelam o snapshot.
 */
@Getter
@ToString
@EqualsAndHashCode(of = "passageiroId")
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Passageiro implements ValueObject {

    @Column(name = "passageiro_id", nullable = false)
    private UUID passageiroId;

    private String nome;
    private String turno;
    private String escola;
    private String matricula;
    private String pontoEmbarque;

    @Column(nullable = false)
    private boolean ativo;

    private Passageiro(UUID passageiroId, String nome, String turno, String escola,
                       String matricula, String pontoEmbarque, boolean ativo) {
        this.passageiroId = passageiroId;
        this.nome = nome;
        this.turno = turno;
        this.escola = escola;
        this.matricula = matricula;
        this.pontoEmbarque = pontoEmbarque;
        this.ativo = ativo;
    }

    public static Passageiro of(UUID passageiroId, String nome, String turno, String escola,
                                String matricula, String pontoEmbarque, boolean ativo) {
        return new Passageiro(passageiroId, nome, turno, escola, matricula, pontoEmbarque, ativo);
    }

    /** Snapshot mínimo (só id, ativo) quando a origem informa apenas o id. */
    public static Passageiro comId(UUID passageiroId) {
        return new Passageiro(passageiroId, null, null, null, null, null, true);
    }

    // ------------------------------------------------------------------
    // Matching por carteirinha — mesma regra do monólito (Viagem):
    // nome é critério obrigatório; turno, escola e matrícula desempatam.
    // ------------------------------------------------------------------

    static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcentos = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcentos.replaceAll("\\s+", " ").trim().toLowerCase();
    }

    static String semPrefixoDeMatricula(String textoNormalizado) {
        if (textoNormalizado == null) {
            return "";
        }
        return textoNormalizado.replaceFirst("^\\d+\\s*-\\s*", "");
    }

    boolean nomeConfereComCarteirinha(Carteirinha carteirinha) {
        if (nome == null) {
            return false;
        }
        return semPrefixoDeMatricula(normalizar(nome))
                .equals(semPrefixoDeMatricula(normalizar(carteirinha.nome())));
    }

    int pontosQueConferemComCarteirinha(Carteirinha carteirinha) {
        int pontos = 0;
        if (turnoConfere(carteirinha.turno())) {
            pontos++;
        }
        if (campoConfere(carteirinha.escola(), escola)) {
            pontos++;
        }
        if (campoConfere(carteirinha.matricula(), matriculaDoPassageiro())) {
            pontos++;
        }
        return pontos;
    }

    private boolean turnoConfere(String turnoCarteirinha) {
        if (turno == null || turnoCarteirinha == null) {
            return false;
        }
        String textoDaCarteirinha = turnoCarteirinha.replaceAll("^\\d+\\s*-\\s*", "").trim();
        return campoConfere(textoDaCarteirinha, turno);
    }

    private boolean campoConfere(String campoCarteirinha, String campoPassageiro) {
        return campoCarteirinha != null && !campoCarteirinha.isEmpty()
                && campoPassageiro != null && !campoPassageiro.isEmpty()
                && normalizar(campoCarteirinha).equals(normalizar(campoPassageiro));
    }

    private String matriculaDoPassageiro() {
        if (matricula != null && !matricula.isEmpty()) {
            return matricula;
        }
        if (nome == null) {
            return null;
        }
        java.util.regex.Matcher matcher =
                java.util.regex.Pattern.compile("^\\s*(\\d+)\\s*-\\s*").matcher(nome);
        return matcher.find() ? matcher.group(1) : null;
    }
}
