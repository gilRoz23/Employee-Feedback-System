package com.hibob.academy.feedbacks_system.dao

import com.hibob.academy.feedbacks_system.CompanyData
import com.hibob.academy.feedbacks_system.CompanyTable
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import org.jooq.Record
import org.jooq.RecordMapper

@Component
class CompanyDao(private val sql: DSLContext) {

    private val companyTable = CompanyTable.instance

    private val companyMapper = RecordMapper<Record, CompanyData> {
            record ->
        CompanyData(record[companyTable.id], record[companyTable.name])
    }

    fun getCompanyByName(companyName: String) : CompanyData? {
        return sql.select(companyTable.id, companyTable.name)
            .from(companyTable)
            .where(companyTable.name.eq(companyName))
            .fetchOne(companyMapper)
    }

    fun insert(companyName: String): Long {
        return sql.insertInto(companyTable)
            .set(companyTable.name, companyName)
            .returning(companyTable.id)
            .fetchOne()!![companyTable.id]
    }

    fun deleteById(companyId: Long): Int {
        return sql.deleteFrom(companyTable)
            .where(companyTable.id.eq(companyId))
            .execute()
    }

}