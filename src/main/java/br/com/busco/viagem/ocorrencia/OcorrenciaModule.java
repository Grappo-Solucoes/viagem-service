package br.com.busco.viagem.ocorrencia;

import org.springframework.modulith.ApplicationModule;

@ApplicationModule(type = ApplicationModule.Type.CLOSED, allowedDependencies = {
        "sk"
})
public interface OcorrenciaModule {
}
