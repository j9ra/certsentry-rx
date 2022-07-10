package pl.grabojan.certsentryrx.data.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("CERT_IDENTITY")
public class CertIdentity  {

	@Id
	@Column("ID")
	private Long id;
	
	@Column("SERIAL_NUMBER")
	private String serialNumber;
	
	@Column("NOT_BEFORE")
	private LocalDateTime  notBefore;
	
	@Column("NOT_AFTER")
	private LocalDateTime notAfter;
	
	@Column("ISSUER")
	private String issuer;
	
	@Column("SUBJECT")
	private String subject;
	
	@Column("PUBLIC_KEY_HASH")
	private String publicKeyHash;
	
	@Column("SIGNATURE_ALGO")
	private String signatureAlgo;
	
	@Column("VALUE") 
	private byte[] value;
	
	@Column("SERVICE_ID")
	private Long serviceId;

}
