package pl.grabojan.certsentryrx.data.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.grabojan.certsentryrx.data.model.EventLog;
import pl.grabojan.certsentryrx.data.model.EventType;
import pl.grabojan.certsentryrx.data.repository.EventLogRepository;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Slf4j
public class EventLogDataService {

	private final EventLogRepository eventLogRepository;
	
	public Mono<EventLog> logEvent(EventType eventType, String eventSource,
			String messageDescription, String usernameContext) {
		
		EventLog el = new EventLog();
		el.setType(eventType);
		el.setSource(eventSource);
		el.setDescription(messageDescription);
		el.setTimestamp(LocalDateTime.now());
		el.setUsernameId(usernameContext);
		
			
		log.debug("Saving EventLog {}", el);	
				
		return eventLogRepository.save(el);
		
	}
	
	public  Mono<EventLog> logInfoEvent(String eventSource, String messageDescription, String usernameContext) {
		return logEvent(EventType.INFO, eventSource, messageDescription, usernameContext);
	}
	
	public  Mono<EventLog> logNoticeEvent(String eventSource, String messageDescription, String usernameContext) {
		return logEvent(EventType.NOTICE, eventSource, messageDescription, usernameContext);
	}
	
	public  Mono<EventLog> logWarnEvent(String eventSource, String messageDescription, String usernameContext) {
		return logEvent(EventType.WARN, eventSource, messageDescription, usernameContext);
	}
	
	public  Mono<EventLog> logErrorEvent(String eventSource, String messageDescription, String usernameContext) {
		return logEvent(EventType.ERROR, eventSource, messageDescription, usernameContext);
	}
	
}
