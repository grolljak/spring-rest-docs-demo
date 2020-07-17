package dk.cngroup.spring.rest.docs.demo

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class WebsiteUser(
        val name: String? = null,
        val email: String? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
}