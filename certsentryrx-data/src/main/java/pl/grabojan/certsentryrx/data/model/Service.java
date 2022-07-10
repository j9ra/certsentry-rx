package pl.grabojan.certsentryrx.data.model;

import java.time.LocalDateTime;
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
@Table("SERVICE")
public class Service {
	
	@Id
	@Column("ID")
	private Long id;
	
	@Column("TYPE")
	private ServiceType type;
	
	@Column("NAME")
	private String name;
	
	@Column("STATUS")
	private ServiceStatus status;
	
	@Column("START_DATE")
	private LocalDateTime startDate;
	
	@Column("DEFINITION_URI")
	private String definitionUri;
	
	@Column("PROVIDER_ID")
	private Long providerId;
	
	@With
    @Transient
    private List<CertIdentity> certIdentities;
	
	@With
    @Transient
	private List<SupplyPoint> supplyPoints;
	
	@With
    @Transient
	private List<ServiceExtension> extensions;
	
}
