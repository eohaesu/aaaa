package com.deotis.digitalars.system.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Sentinel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author jongjin
 * @description configuration of redis cluster or standalone mode
 */
@Configuration
@RequiredArgsConstructor
public class DataRedisConfig{
	
	@Value("${system.data.redis.type}")
	private String REDIS_TYPE;
	
	@Value("${system.data.redis.mode}")
	private String REDIS_MODE;
	
	private final RedisProperties redisProperties;
	
	@Bean
	RedisConnectionFactory redisConnectionFactory() {
		
		if("cluster".equals(REDIS_MODE)) {
			//Amazon elastic cache cluster node
			if("aws".equals(REDIS_TYPE)) {
				RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
				redisClusterConfiguration.clusterNode(redisProperties.getHost(), redisProperties.getPort());
				
				ClusterTopologyRefreshOptions topologyRefershOptions = ClusterTopologyRefreshOptions
						.builder()
						.closeStaleConnections(true)
						.enableAllAdaptiveRefreshTriggers().build();
				
				ClusterClientOptions clusterClientOptions = ClusterClientOptions
						.builder()
						.autoReconnect(true)
						.topologyRefreshOptions(topologyRefershOptions)
						.build();
						
				LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration
						.builder()
						.readFrom(ReadFrom.MASTER_PREFERRED)
						.clientOptions(clusterClientOptions)
						.build();
				
				LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisClusterConfiguration, clientConfiguration);
				lettuceConnectionFactory.afterPropertiesSet();
				
				return lettuceConnectionFactory;
			//Azure cache cluster node	
			}else if("azure".equals(REDIS_TYPE)) {
				RedisClusterConfiguration configuration = new RedisClusterConfiguration();
		        configuration.addClusterNode(new RedisNode(redisProperties.getHost(), redisProperties.getPort()));
		        //configuration.setPassword(RedisPassword.of(primary_access_key));
				
				ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
		                .enablePeriodicRefresh(Duration.ofMinutes(10L))
		                .enableAllAdaptiveRefreshTriggers()
		                .build();

		        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
		                .useSsl().and()
		                .commandTimeout(Duration.ofMillis(3000))
		                .clientOptions(ClusterClientOptions.builder().topologyRefreshOptions(topologyRefreshOptions).build())
		                .build();

		        return new LettuceConnectionFactory(configuration, clientConfig);
			//vanilla redis cluster node	
			}else {
				RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
				redisProperties.getCluster().getNodes().forEach(s -> {
		            String[] ip = s.split(":");
		            redisClusterConfiguration.clusterNode(ip[0],Integer.parseInt(ip[1]));
		        });
				if(StringUtils.hasLength(redisProperties.getPassword())) {
					redisClusterConfiguration.setPassword(redisProperties.getPassword());
				}
				
				ClusterTopologyRefreshOptions topologyRefershOptions = ClusterTopologyRefreshOptions
						.builder()
						.closeStaleConnections(true)
						.enableAllAdaptiveRefreshTriggers().build();
				
				ClusterClientOptions clusterClientOptions = ClusterClientOptions
						.builder()
						.autoReconnect(true)
						.topologyRefreshOptions(topologyRefershOptions)
						.build();
						
				LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration
						.builder()
						.readFrom(ReadFrom.MASTER_PREFERRED)
						.clientOptions(clusterClientOptions)
						.build();
				
				
				return new LettuceConnectionFactory(redisClusterConfiguration, clientConfiguration);
			}
		//Sentinel Redis mode	
		}else if("sentinel".equals(REDIS_MODE)) {
			
			List<RedisNode> nodes = new ArrayList<>();
			
			Sentinel sentinel = redisProperties.getSentinel();
			
			for (String hostAndPort : sentinel.getNodes()) {
				String[] args = StringUtils.split(hostAndPort, ":");
				nodes.add(new RedisNode(args[0], Integer.valueOf(args[1])));
			}
			
			RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration().master(sentinel.getMaster());
			
			sentinelConfig.setSentinels(nodes);
			
			if(StringUtils.hasLength(redisProperties.getPassword())) {
				sentinelConfig.setPassword(redisProperties.getPassword());
			}

			return new LettuceConnectionFactory(sentinelConfig);

		// StandAlone redis mode
		} else {
			RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
			redisStandaloneConfiguration.setHostName(redisProperties.getHost());
			redisStandaloneConfiguration.setPort(redisProperties.getPort());
			if (StringUtils.hasLength(redisProperties.getPassword())) {
				redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
			}
			redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());

			ClientOptions clientOption = ClientOptions 
					.builder()
					.autoReconnect(true)
					.build();
					
			LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration
					.builder()
					.clientOptions(clientOption)
					.build();

			return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfiguration);
		}

	}
	//Base redisTemplate
	@Bean
	RedisTemplate<?, ?> redisTemplate(){
		RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());

		return redisTemplate;
	}

}