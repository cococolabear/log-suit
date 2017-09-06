package com.superad.log.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import com.superad.log.core.LogConstant;
import com.superad.log.monitor.HttpMonitor;
import com.superad.log.stream.ClickLogStreamBroker;
import com.superad.log.stream.InstallLogStreamBroker;

@SpringBootApplication
@ComponentScan(value = "com.superad.log")
public class LogSuitApplication implements ApplicationContextAware {

	private ExecutorService pool = Executors.newFixedThreadPool(3);

	@Resource
	private Environment environment;

	@Bean
	public JdbcTemplate jdbcTemplate() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(environment.getProperty("spring.datasource.url"));
		dataSource.setUsername(environment.getProperty("spring.datasource.username"));
		dataSource.setPassword(environment.getProperty("spring.datasource.password"));
		dataSource.setInitialSize(LogConstant.INIT_SIZE);
		dataSource.setMaxTotal(LogConstant.CONN_SIZE);
		dataSource.setMaxOpenPreparedStatements(LogConstant.CONN_SIZE);
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setValidationQueryTimeout(LogConstant.VALIDATE_TIME);
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(dataSource);
		return jdbcTemplate;
	}

	@Bean(destroyMethod = "close")
	public RocksDB rocksdb() throws RocksDBException {
		RocksDB.loadLibrary();
		Options options = new Options();
		options.setEnableWriteThreadAdaptiveYield(true);
		options.setAllowConcurrentMemtableWrite(true);
		options.setAdviseRandomOnOpen(true);
		options.setAllowMmapReads(true);
		options.setAllowMmapWrites(true);
		options.setCreateIfMissing(true);
		RocksDB rocksDB = RocksDB.open(options, environment.getProperty("application.rocksdb.path"));
		return rocksDB;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) {
		pool.execute(context.getBean(HttpMonitor.class));
		pool.execute(context.getBean(ClickLogStreamBroker.class));
		pool.execute(context.getBean(InstallLogStreamBroker.class));
	}

	public static void main(String[] args) {
		SpringApplication.run(LogSuitApplication.class, args);
	}
}
