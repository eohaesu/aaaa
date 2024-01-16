package com.deotis.digitalars.mapper.rest;

import org.springframework.data.repository.CrudRepository;

import com.deotis.digitalars.model.wms.WmsInternalRedisEntity;

public interface WmsInternalRedisMapper extends CrudRepository<WmsInternalRedisEntity, String> {}
