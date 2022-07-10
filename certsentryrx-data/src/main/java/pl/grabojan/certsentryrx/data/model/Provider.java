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
@Table("PROVIDER")
public class Provider  {
	
	@Id
	@Column("ID")
	private Long id;
	
	@Column("NAME")
	private String name;
	
	@Column("TRADE_NAME")
	private String tradeName;
	
	@Column("INFORMATION_URI")
	private String informationUri;
	
	@Column("TRUSTED_LIST_ID")
	private Long trustedListId;
	
	@With
    @Transient
	private List<Service> services;
	
}
