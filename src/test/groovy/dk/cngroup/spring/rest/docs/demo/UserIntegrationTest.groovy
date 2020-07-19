package dk.cngroup.spring.rest.docs.demo

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

import javax.transaction.Transactional

import static org.hamcrest.Matchers.hasSize
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import static org.springframework.restdocs.payload.JsonFieldType.STRING
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import static org.springframework.restdocs.request.RequestDocumentation.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class UserIntegrationTest extends BaseIntegrationTest {

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
        def result = mockMvc.perform(get("/users?page=0&size=2&sort=name,asc"))


        then:
        result.andExpect(status().isOk())
        result.andExpect(jsonPath('$._embedded.users', hasSize(2)))

        and:
        result.andDo(document("user-get", requestParameters(
                parameterWithName("page").description("Page number"),
                parameterWithName("size").description("Page size"),
                parameterWithName("sort").description("Sorting in a form of \"field name,[asc/desc]\"")
        ), responseCommonHateoasFields("users",
                fieldWithPath("name").type(STRING).description("The user name"),
                fieldWithPath("email").type(STRING).description("The email"),
        ),
                commonHateoasLinks()))
    }

    def "when user is created via POST, then created status is returned"() {

        given:
        def userRequest = [name: 'Jon Doe', email: 'jon.doe@gmail.com']

        when:
        def result = mockMvc.perform(post("/users")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(userRequest))
        )

        then:
        result.andExpect(status().isCreated())

        and:
        result.andDo(document("user-post",
                requestFields(
                        fieldWithPath("name").type(STRING).description("The user name"),
                        fieldWithPath("email").type(STRING).description("The email"),
                )))
    }

    def 'when deleting user via DELETE, then no content status is returned'() {
        given:
        def user = repository.save(new WebsiteUser("Jon Doe", "jon.doe@email.com"))

        when:
        def result = mockMvc.perform(delete("/users/{id}", user.id))

        then:
        result.andExpect(status().isNoContent())

        and:
        result.andDo(document("user-delete",
                pathParameters(
                        parameterWithName("id").description("The id of the user to be deleted")
                )
        ))
    }

    def 'when updating user via PATCH, then the user is updated'() {
        given:
        def user = repository.save(new WebsiteUser("Jon Doe", "jon.doe@email.com"))
        def userRequest = [name: 'Jon Poe', email: 'jon.poe@gmail.com']

        when:
        def result = mockMvc.perform(patch("/users/{id}", user.id)
                .content(mapper.writeValueAsString(userRequest)))

        then:
        result.andExpect(status().isOk())
        repository.findById(user.id).get().name == 'Jon Poe'

        and:
        result.andDo(document("user-patch",
                pathParameters(
                        parameterWithName("id").description("The id of the unknown zip city to be deleted")
                ),
                requestFields(
                        fieldWithPath("name").type(STRING).description("The user name"),
                        fieldWithPath("email").type(STRING).description("The email"),
                )))
    }

}
