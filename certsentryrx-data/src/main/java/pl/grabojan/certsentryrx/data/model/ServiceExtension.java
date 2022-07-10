package pl.grabojan.certsentryrx.data.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("EXTENSION")
public class ServiceExtension {
	
	@Id
	@Column("ID")
	private Long id;
	
	@Column("NAME")
	private String name;
	
	@Column("TYPE")
	private ExtensionType type;
	
	@Column("VALUE")
	private String value;

	@Column("SERVICE_ID")
	private Long serviceId;
	
}
