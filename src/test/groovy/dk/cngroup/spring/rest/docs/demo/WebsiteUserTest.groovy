package dk.cngroup.spring.rest.docs.demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.hamcrest.Matchers.hasSize
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class WebsiteUserTest extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    UserRepository repository

    def "basic get request"() {
        given:
        repository.saveAll([
                new WebsiteUser(1, "Jon Doe", "jon.doe@email.com"),
                new WebsiteUser(2, "Jane Doe", "jane.doe@email.com")
        ])

        when:
        def result = mockMvc.perform(get("/users"))


        then:
        result.andExpect(status().isOk())
        result.andExpect(jsonPath('$._embedded.users', hasSize(2)))

        String content = result.andReturn().getResponse().getContentAsString();
        println content
    }
}
