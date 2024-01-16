package com.deotis.digitalars.service.rest.external;

import java.util.Arrays;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.deotis.digitalars.system.rest.client.DeotisReactiveClient;
import com.deotis.digitalars.system.rest.client.DeotisTemplate;
import com.deotis.digitalars.util.collection.DMap;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

/**
 * 
 * @author jongjin
 * @description service for external sample
 */
@Slf4j
@AllArgsConstructor
@Service
public class ExternalSampleService {

	private final DeotisTemplate deotisTemplate;
	private final DeotisReactiveClient deotisReactiveClient;
	
	/**
	 * DeotisTemplate block sample
	 * @return void
	 */
	public Map<String, Object> getRestTemplateSample() {
		
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("https://reqres.in/api/users");
		
		/* for HttpMethod.GET url param
		uriBuilder.queryParam("param1", "value");
		uriBuilder.queryParam("param2", "value");
		*/
		
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		
		map.add("name", "jongjin");
		map.add("job", "deotis");

		HttpHeaders headers = new HttpHeaders();
		
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		HttpEntity<?> httpEntity = new HttpEntity<>(map, headers);
		
		log.debug(uriBuilder.build().toUriString());
		
		Map<String, Object> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
		
		/* entity result sample
		ResponseEntity<SampleEntity> response = 
				deotisTemplate.exchange(uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<SampleEntity>() {});
		*/

		log.debug("response:{}", response);

		return response;
	}
	
	/**
	 * DeotisReactiveClient non-block sample
	 * @return void
	 */
	public DMap<String, Object> getReactiveClientSample() {
		
		Mono<Map<String, Object>> categoryMono = callApiFirst("3").subscribeOn(Schedulers.boundedElastic());

		Mono<Map<String, Object>> urlMono = callApiSecond("jongjin").subscribeOn(Schedulers.boundedElastic());
			
		Tuple2<Map<String, Object>, Map<String, Object>> tuple2 = Mono.zip(categoryMono, urlMono).block();	
		
		DMap<String, Object> result = new DMap<String, Object>();
		
		result.put("t1", tuple2 != null ? tuple2.getT1() : null);
		result.put("t2", tuple2 != null ? tuple2.getT2() : null);
		
		return result;
	}
	
	private Mono<Map<String, Object>> callApiFirst(String id){
		return deotisReactiveClient.create().mutate()
			.baseUrl("https://reqres.in/api/users")
			.build()
			.get()
			.uri(uriBuilder -> uriBuilder
				//.path("/products/{id}/attributes/{attributeId}") pathVariable sample
				//.build(2, 13))
				.queryParam("delay", id)
				.build())
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
	}
	
	private Mono<Map<String, Object>> callApiSecond(String name){
		
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		
		map.add("name", name);
		map.add("job", "deotis");

		HttpHeaders headers = new HttpHeaders();
		
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		HttpEntity<?> httpEntity = new HttpEntity<>(map, headers);
		
		return deotisReactiveClient.create().mutate()
			.baseUrl("https://reqres.in/api/users")
			.build()
			.post()
			.bodyValue(httpEntity)
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
	}
}