package pl.grabojan.certsentryrx.restapi.endpoint;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class RequestsValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		
		return ValidationRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "cert", "cert.empty");
		
		ValidationRequest req = (ValidationRequest)target;
		if(req.getProfile() != null && req.getProfile().length() > 0) {
			if(req.getProfile().length() > 16) {
				errors.rejectValue("profile", "profile.tolong");
			}
		}
		
		
	}

}
