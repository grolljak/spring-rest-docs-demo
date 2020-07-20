package dk.cngroup.spring.rest.docs.demo

import org.springframework.restdocs.hypermedia.LinkDescriptor
import org.springframework.restdocs.hypermedia.LinksSnippet
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.ResponseFieldsSnippet
import spock.lang.Specification

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links
import static org.springframework.restdocs.payload.JsonFieldType.*
import static org.springframework.restdocs.payload.PayloadDocumentation.*

class BaseIntegrationTest extends Specification {

    static ResponseFieldsSnippet responseCommonHateoasFields(String basePath, FieldDescriptor... fieldDescriptors) {

        FieldDescriptor[] descriptors = [
                fieldWithPath("_embedded").ignored(),
                fieldWithPath("_embedded.${basePath}[]").type(ARRAY).description("All users"),
        ]
        descriptors += applyPathPrefix("_embedded.${basePath}[].", fieldDescriptors.toList())
        descriptors += [
                subsectionWithPath("_embedded.${basePath}[]._links").ignored(),

                subsectionWithPath("_links").type(OBJECT).description("Links section"),
                fieldWithPath("page").type(OBJECT).description("Page section"),
                fieldWithPath("page.size").type(NUMBER).description("Page size"),
                fieldWithPath("page.totalElements").type(NUMBER).description("Number of all elements"),
                fieldWithPath("page.totalPages").type(NUMBER).description("Number of all pages"),
                fieldWithPath("page.number").type(NUMBER).description("Page number")
        ]

        return responseFields(descriptors)
    }

    static LinksSnippet commonHateoasLinks() {
        LinkDescriptor[] descriptors = [
                linkWithRel("self").description("The self link"),
                linkWithRel("profile").description("The profile link"),
                linkWithRel("search").description("The search link")]
        return links(descriptors)
    }

}
