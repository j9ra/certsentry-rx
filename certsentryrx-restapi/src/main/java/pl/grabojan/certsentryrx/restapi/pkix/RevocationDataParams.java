package pl.grabojan.certsentryrx.restapi.pkix;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RevocationDataParams {

	private String ocspURI;
	private String crlURI;
	
	public boolean hasOcsp() {
	
		return ocspURI != null && ocspURI.length() > 0;
	}
	
	public boolean hasCrl() {
		return crlURI != null && crlURI.length() > 0;
	}
	
}
