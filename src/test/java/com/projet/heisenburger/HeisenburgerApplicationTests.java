package com.projet.heisenburger;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.projet.heisenburger.repository.CommandeRepository;
import com.projet.heisenburger.repository.RestaurantRepository;
import com.projet.heisenburger.repository.UserRepository;
import com.projet.heisenburger.repository.PlatRepository;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
class HeisenburgerApplicationTests {

	@Test
	void contextLoads() {
	}

	@MockBean
	private CommandeRepository commandeRepository;

	@MockBean
	private RestaurantRepository restaurantRepository;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private PlatRepository platRepository;

}
