package pl.grabojan.certsentryrx.data.model;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("SEC_ROLE")
public class SecRole {

	@Column("USERNAME")
	private String username;
	
	@Column("AUTHORITY")
	private String authority;
}
