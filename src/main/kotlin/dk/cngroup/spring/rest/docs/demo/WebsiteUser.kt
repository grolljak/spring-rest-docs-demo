package dk.cngroup.spring.rest.docs.demo

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class WebsiteUser(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
        val name: String? = null,
        val email: String? = null
)