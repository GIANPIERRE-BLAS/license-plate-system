package com.colegiocesarvallejo.matriculas_platform.service.notificaciones;

import com.colegiocesarvallejo.matriculas_platform.dto.dashboard.NotificacionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final SimpMessagingTemplate messagingTemplate;

    public void enviarNotificacion(Long usuarioId, String titulo, String mensaje) {
        NotificacionDTO notificacion = new NotificacionDTO();
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setTipo("info");
        notificacion.setLeida(false);
        notificacion.setFechaCreacion(LocalDateTime.now());

        messagingTemplate.convertAndSendToUser(
                usuarioId.toString(),
                "/queue/notifications",
                notificacion
        );

        log.info("Notificación enviada a usuario {}: {}", usuarioId, titulo);
    }

    public void enviarNotificacionGlobal(String titulo, String mensaje, String tipo) {
        NotificacionDTO notificacion = new NotificacionDTO();
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setTipo(tipo);
        notificacion.setLeida(false);
        notificacion.setFechaCreacion(LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/notifications", notificacion);
        log.info("Notificación global enviada: {}", titulo);
    }
}
