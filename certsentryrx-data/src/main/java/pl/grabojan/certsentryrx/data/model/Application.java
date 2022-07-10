package pl.grabojan.certsentryrx.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("APPLICATION")
public class Application {

	@Id
	@Column("ID")
	private Long id;
	
	@Column("NAME")
	private String name;
	
	@Column("DESCRIPTION")
	private String description;
	
	@Column("API_KEY")
	private String apiKey; 
	
}
