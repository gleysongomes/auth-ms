package io.github.gleysongomes.auth.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.github.gleysongomes.auth.dto.UsuarioEventDto;
import io.github.gleysongomes.auth.enums.TipoAcao;

@Component
public class UsuarioEventPublisher {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Value("${auth.broker.exchange.usuario-event}")
	private String exchangeUsuarioEvent;

	public void publishUsuarioEvent(UsuarioEventDto usuarioEventDto, TipoAcao tipoAcao) {
		usuarioEventDto.setTipoAcao(tipoAcao.toString());
		rabbitTemplate.convertAndSend(exchangeUsuarioEvent, "", usuarioEventDto);
	}

}
