package pl.grabojan.certsentryrx.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("Profile")
public class Profile {
	
	@Id
	@Column("ID")
	private Long id;
	
	@Column("NAME")
	private String name;
	
	@Column("TERRITORY")
	private String territory;
	
	@Column("PROVIDER")
	private String provider;
	
	@Column("SERVICE_INFO")
	private String serviceInfo;
	
}
