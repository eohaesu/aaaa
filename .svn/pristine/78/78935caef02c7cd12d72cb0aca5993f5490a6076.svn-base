package com.deotis.digitalars.security.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthorizationEntity implements UserDetails {

	private static final long serialVersionUID = 6981946228893411204L;
	
	private String password;
	private String username;
	private Set<GrantedAuthority> authorities;
	private SecretEntity secret;
	
	@Builder
	public AuthorizationEntity(String sid, Collection<? extends GrantedAuthority> authorities, String password
								,SecretEntity secret
			) {
		this.username = sid;
		Set<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
		
		for (GrantedAuthority grantedAuthority : authorities) {
			roles.add(grantedAuthority);
		}
		
		this.authorities = roles;
		this.password = password;
		this.secret = secret;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	@Override
	public String getPassword() {
		return password;
	}
	@Override
	public String getUsername() {
		return username;
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
		return true;
	}
	@Override
	public boolean equals(Object rhs) {
		if (rhs instanceof AuthorizationEntity) {
			return username.equals(((AuthorizationEntity) rhs).username);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return username.hashCode();
	}
}
