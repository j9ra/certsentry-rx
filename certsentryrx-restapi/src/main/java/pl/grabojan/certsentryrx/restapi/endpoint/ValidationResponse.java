package pl.grabojan.certsentryrx.restapi.endpoint;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ValidationResponse {

	private String status;
	
	private String reasonMessage;
	
	private List<String> certPath = new ArrayList<>();
	
	private String ref;
	
	public static ValidationResponse valid(String ref, List<String> stringPath) {
		ValidationResponse resp = new ValidationResponse();
		resp.setRef(ref);
		resp.setCertPath(stringPath);
		resp.setReasonMessage("");
		resp.setStatus("VALID");
		return resp;
	}
	
	public static ValidationResponse invalid(String ref, List<String> stringPath, String mesg) {
		ValidationResponse resp = new ValidationResponse();
		resp.setRef(ref);
		resp.setCertPath(stringPath);
		resp.setReasonMessage(mesg);
		resp.setStatus("INVALID");
		return resp;
	}
	
}
