package com.deotis.digitalars.system.config.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 
 * @author jongjin
 * @description override datasourceLookUp for dynamic datasource
 */

public class DynamicDataSource extends AbstractRoutingDataSource {
	
	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceContext.getDataSourceType();
	}
}
