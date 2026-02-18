package br.com.busco.viagem.infra.gateway;

import br.com.busco.viagem.ocorrencia.app.UsuarioAutenticadoGateway;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Component
public class UsuarioAutenticadoGatewayImpl implements UsuarioAutenticadoGateway {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public UUID getUserId() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            String userId = servletAttributes.getRequest().getHeader(USER_ID_HEADER);
            if (userId != null && !userId.isBlank()) {
                return UUID.fromString(userId);
            }
        }

        throw new EntityNotFoundException("Usuario autenticado nao encontrado.");
    }
}
