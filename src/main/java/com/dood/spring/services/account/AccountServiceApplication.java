package com.dood.spring.services.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The accounts web-application. This class has two uses:
 * <ol>
 * <li>Provide configuration and setup for {@link AccountsServer} ... or</li>
 * <li>Run as a stand-alone Spring Boot web-application for testing (in which
 * case there is <i>no</i> microservice registration</li>
 * </ol>
 * <p>
 * To execute as a microservice, run {@link AccountsServer} instead.
 *
 * @author Paul Chapman
 */
@SpringBootApplication
@EntityScan("com.dood.spring.services.account.model")
@EnableJpaRepositories("com.dood.spring.services.account.repository")
@PropertySource("classpath:db-config.properties")
public class AccountServiceApplication {
	private static Logger LOG = LoggerFactory.getLogger(AccountServiceApplication.class);

    public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

	/**
	 * Creates an in-memory "rewards" database populated with test data for fast
	 * testing
	 */
	@Bean
	public DataSource dataSource() {
		LOG.info("dataSource() invoked");

		// Create an in-memory H2 relational database containing some demo
		// accounts.
		DataSource dataSource = (new EmbeddedDatabaseBuilder())
				.addScript("classpath:testdb/schema.sql")
				.addScript("classpath:testdb/data.sql").build();

		LOG.info("dataSource = " + dataSource);

		// Sanity check
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<Map<String, Object>> accounts = jdbcTemplate
				.queryForList("SELECT number FROM T_ACCOUNT");
		LOG.info("System has " + accounts.size() + " accounts");

		// Populate with random balances
		Random rand = new Random();

		for (Map<String, Object> item : accounts) {
			String number = (String) item.get("number");
			BigDecimal balance = new BigDecimal(rand.nextInt(10000000) / 100.0)
					.setScale(2, BigDecimal.ROUND_HALF_UP);
			jdbcTemplate.update(
					"UPDATE T_ACCOUNT SET balance = ? WHERE number = ?",
					balance, number);
		}

		return dataSource;
	}
}
