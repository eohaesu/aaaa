package com.deotis.digitalars.service.business;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.deotis.digitalars.mapper.business.MainMapper;
import com.deotis.digitalars.model.SiteInfo;
import com.deotis.digitalars.util.collection.DMap;

/**
 * 
 * @author jongjin
 * @description service for main
 */
@Service
public class MainService {
	
	@Value("${system.scheduled.logfile.directory}")
	public static String LOG_FILE_LOCATION;	

	private final SqlSessionTemplate sqlSession;
	
	public MainService(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

	
	/**
	 * 사이트 정보 조회
	 * @param DMap<String, Object>
	 * @return SiteInfo
	 */
	public SiteInfo getSiteInfo(DMap<String, Object> params) {

		MainMapper mapper = sqlSession.getMapper(MainMapper.class);

		return mapper.getSiteInfo(params);
	}

}