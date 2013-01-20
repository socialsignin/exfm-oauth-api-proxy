package org.socialsignin.exfmproxy.conf;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration
public class DataSourceConfig {

	 /**
	  * 
	  * @return the DataSource used by JdbcTokenStore to store oauth tokens
	  */
	 @Bean
	 public DataSource dataSource() {
	        return new EmbeddedDatabaseBuilder()
	            .setType(EmbeddedDatabaseType.HSQL)
	            .addScript("JdbcTokenStore.sql")
	            .build();
	    }
	
}
