package org.yorubaname.dictionary.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.yorubaname.dictionary.DictionaryApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DictionaryApplication.class)
@WebAppConfiguration
public class DictionaryApplicationTests {

	@Test
	public void contextLoads() {
	}

}
