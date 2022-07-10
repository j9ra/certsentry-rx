package pl.grabojan.certsentryrx.restapi.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

import pl.grabojan.certsentryrx.data.model.SecRole;
import pl.grabojan.certsentryrx.data.model.SecUser;
import pl.grabojan.certsentryrx.data.service.SecUserAppDataService;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
	    return http
	        .authorizeExchange()
	          .anyExchange().hasRole("USER")
	       .and()
	       	.httpBasic()
	       .and()
	      	.anonymous().disable()
	      	.formLogin().disable()
	      	.logout().disable()
	      	.csrf().disable()
	        .build();
	  }
	
	@Bean
	public ReactiveUserDetailsService userDetailsService(SecUserAppDataService userRepo) {
	  return new ReactiveUserDetailsService() {
	    @Override
	    public Mono<UserDetails> findByUsername(String username) {
	      return userRepo.getUserByName(username)
	        .map(user -> {
	          return new SimpleUserDetails(user);
	        });
	    }
	  };
	}
	
	
	private static class SimpleUserDetails implements UserDetails {
		
		private static final long serialVersionUID = 1L;
		
		private final SecUser secUser;

		SimpleUserDetails(SecUser secUser) {
			this.secUser = secUser;
		}
		
		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			
			List<SimpleGrantedAuthority> authoritiesRet = new ArrayList<SimpleGrantedAuthority>();
			List<SecRole> roles = secUser.getAuthorities();
			roles.forEach(r -> authoritiesRet.add(new SimpleGrantedAuthority(r.getAuthority())));
			return authoritiesRet;
		}

		@Override
		public String getPassword() {
			return secUser.getPassword();
		}

		@Override
		public String getUsername() {
			return secUser.getUsername();
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return secUser.getEnabled();
		}
		
	}
	
	
}
