package com.deotis.digitalars.system.rest.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

/**
 * 
 * @author jongjin
 * @description non-block rest connector of reactive webFlux
 */
@Slf4j
public class DeotisReactiveClient {
	
	private HttpClient httpClient;

	public DeotisReactiveClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
	//webClient(reactive non-blocking)
	public WebClient create() {
	
		WebClient client = WebClient.builder()
				.defaultHeaders(httpHeaders -> {
			          httpHeaders.addAll(defaultHeaders());
		        })
				//.filter(logRequest())
				.filters(exchangeFilterFunctions -> {
				   exchangeFilterFunctions.add(logRequest());
				   exchangeFilterFunctions.add(logResponse());
				})
				.clientConnector(new ReactorClientHttpConnector(this.httpClient))
				.build();

		return client;
	}
	
	//default header configuration
	private HttpHeaders defaultHeaders() {
		
	    HttpHeaders headers = new HttpHeaders(); 
	    return headers;
	}
	//log request filter
	private ExchangeFilterFunction logRequest() {
		
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("===== Reactive Request info: {} {}", clientRequest.method(), clientRequest.url());
            
            clientRequest.headers().forEach((name, values) -> 
            values.forEach(value -> 
            log.info("===== Request Header: {} {}", name, value)));
            
            return Mono.just(clientRequest);
        });
    }
	
	// log response only header info (not body)
	private ExchangeFilterFunction logResponse() {
		
		return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
			
			HttpStatus status = clientResponse.statusCode();
			log.debug("=====Response Staus : {}", status.value());
			
			clientResponse.headers()
            .asHttpHeaders()
            .forEach((name, values) -> 
            values.forEach(value -> log.trace("=====Response Header : {} , {}", name, value)));
            
			if (clientResponse.statusCode() != null) {
				return clientResponse.bodyToMono(String.class)
						.flatMap(body -> {
							log.debug("=====Response Body : {}", body);						
							return Mono.just(clientResponse);
						});
			} else {
				return Mono.just(clientResponse);
			}
        });
    }
}