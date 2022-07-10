package pl.grabojan.certsentryrx.data.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("EVENT_LOG")
public class EventLog  {
	
	@Id
	@Column("ID")
	private Long id;
	
	@Column("TIMESTAMP")
	private LocalDateTime timestamp;
	
	@Column("TYPE")
	private EventType type;
	
	@Column("SOURCE")
	private String source;
	
	@Column("DESCRIPTION")
	private String description;
	
	@Column("USERNAME_ID")
	private String usernameId;
	
}
