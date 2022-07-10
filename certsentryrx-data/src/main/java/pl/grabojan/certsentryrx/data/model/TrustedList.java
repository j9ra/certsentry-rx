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
@Table("TRUSTED_LIST")
public class TrustedList {
	
	@Id
	@Column("ID")
	private Long id;
	
	@Column("TYPE")
	private TrustedListType type;
	
	@Column("SEQUENCE_NUMBER")
	private Long sequenceNumber;
	
	@Column("OPERATOR_NAME")
	private String operatorName;
	
	@Column("DISTRIBUTION_POINT")
	private String distributionPoint;
	
	@Column("INFORMATION_URI")
	private String informationUri;
	
	@Column("TERRITORY")
	private String territory;

	@Column("LIST_ISSUE")
	private LocalDateTime listIssue;
	
	@Column("NEXT_UPDATE")
	private LocalDateTime nextUpdate;
	
	@Column("LIST_HASH")
	private String listHash;
	
	@Column("LOCAL_URI")
	private String localUri;
	
	@Column("LAST_CHECK")
	private LocalDateTime lastCheck;
	
	@Column("IS_VALID")
	private Boolean isValid;
	
	@With
    @Transient
	private List<Provider> providers;

	@With
    @Transient
	private List<TrustedListUpdate> trustedListUpdates;
	
}
