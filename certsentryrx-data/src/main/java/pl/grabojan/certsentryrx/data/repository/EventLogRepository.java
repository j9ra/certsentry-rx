package pl.grabojan.certsentryrx.data.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pl.grabojan.certsentryrx.data.model.EventLog;
import reactor.core.publisher.Flux;


public interface EventLogRepository extends ReactiveCrudRepository<EventLog, Long> {

	Flux<EventLog> findByTimestampBetween(LocalDateTime begin, LocalDateTime end, Pageable pageable);
}
