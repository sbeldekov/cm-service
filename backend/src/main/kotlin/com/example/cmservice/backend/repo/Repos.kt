package com.example.cmservice.backend.repo

import com.example.cmservice.common.annotation.ExcludeFromJacocoGeneratedReport
import org.hibernate.envers.Audited
import org.hibernate.envers.RevisionEntity
import org.hibernate.envers.RevisionNumber
import org.hibernate.envers.RevisionTimestamp
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.data.repository.history.RevisionRepository
import java.io.Serializable
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseEntity<T : Serializable> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: T? = null

    @Version
    @Column(nullable = false)
    val version: Long? = null

    @CreatedDate
    @Column(nullable = false)
    var createdDate: Instant? = null

    @LastModifiedDate
    @Column(nullable = false)
    var lastModifiedDate: Instant? = null
}

@Entity
@Audited(withModifiedFlag = true)
class Company(

    @Column(nullable = false, unique = true, length = 100)
    var name: String? = null

) : BaseEntity<Long>()

@Entity
@RevisionEntity
@Suppress("unused")
@ExcludeFromJacocoGeneratedReport
class RevInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    val id: Long? = null

    @RevisionTimestamp
    @Column(nullable = false)
    val timestamp: Long? = null
}

interface CompanyRepository :
    JpaRepositoryImplementation<Company, Long>,
    RevisionRepository<Company, Long, Long>
