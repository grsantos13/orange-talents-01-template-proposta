package br.com.zup.propostas.configuracao;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .authorizeRequests(authz -> authz
                    .antMatchers("/propostas").permitAll()
                    .anyRequest().permitAll()).csrf().disable()
                    .headers().frameOptions().sameOrigin().and()
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    }
}
