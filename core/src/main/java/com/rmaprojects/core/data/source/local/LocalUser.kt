package com.rmaprojects.core.data.source.local

import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.enumpref.enumOrdinalPref
import com.rmaprojects.core.utils.UserRole

object LocalUser: KotprefModel() {
    var id by nullableStringPref()
    var role by enumOrdinalPref(UserRole.NONE)
    var studentId by nullableStringPref()

    override fun clear() {
        super.clear()
        id = null
        role = UserRole.NONE
        studentId = null
    }
}