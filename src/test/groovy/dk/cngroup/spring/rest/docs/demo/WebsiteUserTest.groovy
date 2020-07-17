package dk.cngroup.spring.rest.docs.demo

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import javax.transaction.Transactional

import static org.hamcrest.Matchers.hasSize
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class WebsiteUserTest extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    UserRepository repository

    @Autowired
    ObjectMapper mapper

    def "when users are requested via GET, correct number of users is returned"() {
        given:
        repository.saveAll([
                new WebsiteUser("Jon Doe", "jon.doe@email.com"),
                new WebsiteUser("Jane Doe", "jane.doe@email.com")
        ])

        when:
        def result = mockMvc.perform(get("/users"))


        then:
        result.andExpect(status().isOk())
        result.andExpect(jsonPath('$._embedded.users', hasSize(2)))
    }

    def "when users are requested via POST, correct number of users is returned"() {

        given:
        def userRequest = [name: 'Jon Doe', email: 'jon.doe@gmail.com']

        when:
        def result = mockMvc.perform(post("/users")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(userRequest))
        )

        then:
        result.andExpect(status().isCreated())
        def string = result.andReturn().response.getContentAsString()
        println string
    }

    def 'when deleting user via DELETE, then no content status is returned'() {
        given:
        def user = repository.save(new WebsiteUser("Jon Doe", "jon.doe@email.com"))

        when:
        def result = mockMvc.perform(delete("/users/{id}", user.id))

        then:
        result.andExpect(status().isNoContent())
    }


}
