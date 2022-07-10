package pl.grabojan.certsentryrx.data.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("SEC_USER")
public class SecUser {
	
	@Id
	@Column("USERNAME")
	private String username;
	
	@Column("PASSWORD")
	private String password;
	
	@Column("ENABLED")
	private Boolean enabled;
	
	@Column("APPLICATION_ID")
	private Long applicationId;
		
	@With
    @Transient
	private List<SecRole> authorities;

	@With
    @Transient
	private Application application;
		
}
