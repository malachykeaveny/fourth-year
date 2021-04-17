package com.example.softwarepatternsca2

open class PrivilegesDecorator(protected var privileges: Privileges) : Privileges {
    override fun hasAdminPrivileges(): Boolean {
        return privileges.hasAdminPrivileges()
    }

}