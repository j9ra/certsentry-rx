package pl.grabojan.certsentryrx.data.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("TRUSTED_LIST_UPDATE")
public class TrustedListUpdate {
		
	@Id
	@Column("ID")
	private Long id;

	@Column("TIMESTAMP")
	private LocalDateTime timestamp;
	
	@Column("STATUS")
	private UpdateStatus status;
	
	@Column("ARCH_LOCAL_URI")
	private String archLocalUri;
	
	@Column("INFO")
	private String info;
	
	@Column("ERROR_CODE")
	private Integer errorCode;
	
	@Column("TRUSTED_LIST_ID")
	private Long trustedListId;
	
}
