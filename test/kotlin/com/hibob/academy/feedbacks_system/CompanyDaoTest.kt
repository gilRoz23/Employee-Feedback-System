package com.hibob.academy.feedbacks_system

import com.hibob.academy.feedbacks_system.dao.CompanyDao
import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@BobDbTest
class CompanyDaoTest @Autowired constructor(private val sql: DSLContext) {

    private val companyDao = CompanyDao(sql)

    @Test
    fun `get company by name when company exists`() {
        val companyId = companyDao.insert("Bob")
        val companyData = companyDao.getCompanyByName("Bob")

        assertEquals("Bob", companyData?.name)

        companyDao.deleteById(companyId)
    }

    @Test
    fun `get company by name when company does not exist`() {
        val companyData = companyDao.getCompanyByName("Telad")
        assertNull(companyData)
    }
}
