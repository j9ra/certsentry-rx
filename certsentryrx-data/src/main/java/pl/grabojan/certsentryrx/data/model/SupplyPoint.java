package pl.grabojan.certsentryrx.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("SUPPLY_POINT")
public class SupplyPoint {
		
	@Id
	@Column("ID")
	private Long id;
	
	@Column("POINT_URI")
	private String pointUri;
	
	@Column("TYPE")
	private SupplyPointType type;

	@Column("SERVICE_ID")
	private Long serviceId;
}
