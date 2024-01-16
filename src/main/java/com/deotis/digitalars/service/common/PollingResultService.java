package com.deotis.digitalars.service.common;

import java.util.Set;

import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

import com.deotis.digitalars.constants.CommonConstants;
import com.deotis.digitalars.model.UserEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description service for main menu
 */
@Slf4j
@AllArgsConstructor
@Service
public class PollingResultService {

	@SuppressWarnings("rawtypes")
	private FindByIndexNameSessionRepository sessionRepository;

	/**
	 * get sessionEntity from repository by usid(wms)
	 * @param String sid(wms usid)
	 * @return UserEntity
	 */
	@SuppressWarnings("unchecked")
	public UserEntity getRepositoryEntityByUSID(String sid) {
		
		UserEntity entity = null;

		if(sessionRepository != null) {

			Set<String> set = sessionRepository.findByIndexNameAndIndexValue(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, sid).keySet();

			if(!set.isEmpty()) {
				
				log.trace("Found the session from FindByIndexNameSessionRepository by USID. USID : [{}]", sid);
				
				Session userSession = sessionRepository.findById(set.stream().findAny().get());

				entity = userSession.getAttribute(CommonConstants.SESSION_NAME);
			}else {
				log.debug("Can not found the session from FindByIndexNameSessionRepository. USID : [{}]", sid);
			}
		}

		return entity;
	}
	
	/**
	 * get sessionEntity from repository by session id
	 * @param String uid (appliction session)
	 * @return UserEntity
	 */
	public UserEntity getRepositoryEntityBySessionId(String uid) {
		
		UserEntity entity = null;

		if(sessionRepository != null) {
			log.trace("Found the session from FindByIndexNameSessionRepository by session id. SESSION ID : [{}]", uid);
			if(sessionRepository.findById(uid) != null) {
				entity = sessionRepository.findById(uid).getAttribute(CommonConstants.SESSION_NAME);
			}
		}
		
		return entity;
	}
}