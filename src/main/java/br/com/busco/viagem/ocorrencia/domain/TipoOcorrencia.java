package br.com.busco.viagem.ocorrencia.domain;

import br.com.busco.viagem.ocorrencia.domain.events.TipoOcorrenciaAlterada;
import br.com.busco.viagem.ocorrencia.domain.events.TipoOcorrenciaCriada;
import br.com.busco.viagem.sk.ddd.AbstractAggregateRoot;
import br.com.busco.viagem.sk.ddd.AbstractEntity;
import br.com.busco.viagem.sk.ids.TipoOcorrenciaId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Table
@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public class TipoOcorrencia extends AbstractEntity<TipoOcorrenciaId> {

    private String tipoOcorrencia;

    @Builder
    private TipoOcorrencia(@NonNull String tipoOcorrencia) {
        super(TipoOcorrenciaId.randomId());
        this.tipoOcorrencia = tipoOcorrencia;
    }

    public void alterarTipoOcorrencia(@NonNull String tipoOcorrencia) {
        this.tipoOcorrencia = tipoOcorrencia;
    }

    public static TipoOcorrencia of(String tipoOcorrencia) {
        return new TipoOcorrencia(tipoOcorrencia);
    }
}
