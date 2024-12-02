package com.hibob.academy.service

import com.hibob.academy.dao.OwnerDao
import com.hibob.academy.dao.OwnerData
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

class OwnerServiceTest {

    private lateinit var ownerDao: OwnerDao
    private lateinit var ownerService: OwnerService

    @BeforeEach
    fun setUp() {
        ownerDao = mock(OwnerDao::class.java)
        ownerService = OwnerService(ownerDao)
    }

    @Test
    fun `get owners by company id smaller than one`() {
        val exception = assertThrows<IllegalArgumentException> {
            ownerService.getOwnersByCompany(0L)
        }
        assertEquals("Company ID must be greater than 0", exception.message)
    }

    @Test
    fun `get owners by company id`() {
        whenever (ownerDao.getAllOwners(1L)).thenReturn(listOf(OwnerData(1L, "Gilad", 1L, "1"), OwnerData(2L, "Bob", 1L, "2")))
        assertEquals(listOf(OwnerData(1L, "Gilad", 1L, "1"), OwnerData(2L, "Bob", 1L, "2")), ownerService.getOwnersByCompany(1L))
        verify(ownerDao).getAllOwners(1L)
    }

    @Test
    fun `add owner with blank name`() {
        val exception = assertThrows<IllegalArgumentException> {
            ownerService.addOwner("", 1L, "1")
        }
        assertEquals("Name and Employee ID cannot be blank", exception.message)
    }

    @Test
    fun `add owner with blank employeeId`() {
        val exception = assertThrows<IllegalArgumentException> {
            ownerService.addOwner("Gilad", 1L, "")
        }
        assertEquals("Name and Employee ID cannot be blank", exception.message)
    }

    @Test
    fun `add owner with company id smaller than one`() {
        val exception = assertThrows<IllegalArgumentException> {
            ownerService.addOwner("Gilad", -1L, "1")
        }
        assertEquals("Invalid Company ID", exception.message)
    }

    @Test
    fun `add owner`() {
        assertDoesNotThrow {
            ownerService.addOwner("Gilad", 1L, "1")
        }

        verify(ownerDao).insertOwner("Gilad", 1L, "1")
    }
}