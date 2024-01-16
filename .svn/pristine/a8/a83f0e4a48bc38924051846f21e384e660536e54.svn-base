package com.deotis.digitalars.service.rest.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.deotis.digitalars.constants.CommonConstants;
import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.util.collection.DMap;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description InBound Event Service from WMS
 */
@Slf4j
@AllArgsConstructor
@Service
public class WmsInternalService {

	@SuppressWarnings("rawtypes")
	private FindByIndexNameSessionRepository sessionRepository;

	/**
	 * wms로부터 callend이벤트 시 sessionRepository로부터 session attribute 추가
	 * @param String sid
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public boolean addCallEndSession(String sid) {
		
		Set<String> set = sessionRepository.findByIndexNameAndIndexValue(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, sid).keySet();
		
		boolean result = false;
		
		if(!set.isEmpty()) {
			
			List<Session> sessionList = getSession(set);
			
			for (Session session : sessionList) {
				
				UserEntity entity = session.getAttribute(CommonConstants.SESSION_NAME);
				
				log.debug("Found on nameSessionRepository session id : [{}], sid : [{}]", session.getId(), sid);
				
				entity.setWmsEventName(CommonConstants.MOS_EVENT_CALLEND);
				entity.setRecieve_callend(true);
				
				session.setAttribute(CommonConstants.SESSION_NAME, entity);
				
				sessionRepository.save(session);

				result = true;
	        }
		}
		return result;
	}
	
	/**
	 * wms로부터 waittime 이벤트 시 sessionRepository로부터 session attribute 추가
	 * @param String sid, int waitTime, int waitCount
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public boolean addWaitTimeSession(String sid, String waitTime, String waitCount) {
		
		Set<String> set = sessionRepository.findByIndexNameAndIndexValue(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, sid).keySet();
		
		boolean result = false;
		
		if(!set.isEmpty()) {
			
			List<Session> sessionList = getSession(set);
			
			for (Session session : sessionList) {
				
				UserEntity entity = session.getAttribute(CommonConstants.SESSION_NAME);
				
				log.debug("found on nameSessionRepository and addWaitTimeSession session id : [{}], sid : [{}]", session.getId(), sid);
				
				entity.setWmsEventName(CommonConstants.MOS_EVENT_WAITTIME);
				entity.setCounselorWaitTime(waitTime);
				entity.setCounselorWaitCount(waitCount);
				
				session.setAttribute(CommonConstants.SESSION_NAME, entity);
				
				sessionRepository.save(session);
				
				result = true;

	        }
		}
		return result;
	}
	
	/**
	 * wms로부터 control 이벤트 시 sessionRepository로부터 session attribute 추가
	 * @param String sid, DMap<String, String> data
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public boolean addIVRControl(String sid, DMap<String, Object> data) {
		
		Set<String> set = sessionRepository.findByIndexNameAndIndexValue(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, sid).keySet();
		
		boolean result = false;
		
		if(!set.isEmpty()) {
			
			List<Session> sessionList = getSession(set);
			
			for (Session session : sessionList) {
				
				UserEntity entity = session.getAttribute(CommonConstants.SESSION_NAME);

				entity.setWmsEventName(CommonConstants.MOS_EVENT_CONTROL);
				entity.setIvrControlInfo(data);
				
				log.debug("IVR Control had found session from repository entity : {}", entity.toString());

				session.setAttribute(CommonConstants.SESSION_NAME, entity);
				
				sessionRepository.save(session);
				
				result = true;

	        }
		}
		return result;
	}
	
	/**
	 * check session bi usid
	 * @param String sid(wms usid)
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public boolean isExistSessionByUSID(String sid) {

		boolean result = false;
		
		if(sessionRepository != null) {

			Set<String> set = sessionRepository.findByIndexNameAndIndexValue(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, sid).keySet();

			if(!set.isEmpty()) {
				
				Optional<String> id = set.stream().findAny();
				
				if(id.isPresent()) {
					Session userSession = sessionRepository.findById(id.get());

					if(userSession.getAttribute(CommonConstants.SESSION_NAME) != null) {
						result = true;
					}
				}
			}
		}

		return result;
	}
	
	/**
	 * check session bi usid
	 * @param String sid(wms usid)
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public String getIcidByUSID(String sid) {

		UserEntity entity = null;
		
		if(sessionRepository != null) {

			Set<String> set = sessionRepository.findByIndexNameAndIndexValue(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, sid).keySet();

			if(!set.isEmpty()) {
				
				Optional<String> id = set.stream().findAny();
				
				if(id.isPresent()) {
					Session userSession = sessionRepository.findById(id.get());

					if(userSession.getAttribute(CommonConstants.SESSION_NAME) != null) {
						entity = userSession.getAttribute(CommonConstants.SESSION_NAME);
					}
				}
			}
		}

		return entity != null ? entity.getIcid() : null;
	}
	
	
	private List<Session> getSession(Set<String> sessionSet) {
		
		List<Session> sessionList = new ArrayList<Session>();
		
		for (Iterator<String> iterator = sessionSet.iterator(); iterator.hasNext(); ) {
			
			String sessionId = iterator.next();
			
			if(StringUtils.hasLength(sessionId)){
				sessionList.add(sessionRepository.findById(sessionId));
			}
	    }
		
		return sessionList;
	}
	
	@SuppressWarnings("unchecked")
	public void deleteSessionRepository(String sid) {
		sessionRepository.findByIndexNameAndIndexValue(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, sid)
		.keySet()
		.forEach(session -> this.sessionRepository.deleteById((String) session));
	}
}