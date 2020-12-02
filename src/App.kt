import data.Activity
import data.ExitCodes.*
import data.RoleResource
import data.Roles
import services.DatabaseWrapper
import services.ParserArguments
import java.math.BigInteger
import java.security.MessageDigest


class App {
    private val db = DatabaseWrapper()

    /* метод хеширования */
    private fun md5(password: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(password.toByteArray())).toString(16).padStart(32, '0')
    }

    /* метод для проверки формата логина */
    private fun isLoginValid(login: String): Boolean {
        val mathResult = Regex("[^a-zA-Z0-9]").find(login)
        if (mathResult != null) return true
        return false

    }


    private fun authentificate(login: String, pass: String): Int {
        val user = db.getUser(login)
        when {
            isLoginValid(login) -> return INVALID_LOGIN_FORM.exitCode
//            !user.isInvalidUser() -> return UNKNOWN_LOGIN.exitCode
            user == null -> return UNKNOWN_LOGIN.exitCode
        }
        return if (user!!.hashPassword == md5(md5(pass) + user.salt))
            SUCCESS.exitCode
        else
            INVALID_PASSWORD.exitCode
    }

    private fun authorization(roleStr: String, res: String, idUser: Long): Int = try {
        val role = Roles.valueOf(roleStr)
        val resource = RoleResource(role = role, resource = res, idUser = idUser)
        if (db.checkAccess(resource))
            SUCCESS.exitCode
        else
            NO_ACCESS.exitCode
    } catch (e: IllegalArgumentException) {
        UNKNOWN_ROLE.exitCode
    }

    private fun accounting(activity: Activity): Int {
        if (!activity.hasValidDate())
            return INCORRECT_ACTIVITY.exitCode
        db.addActivity(activity)

        return SUCCESS.exitCode
    }

    fun run(args: Array<String>): Int {

        val parser = ParserArguments("handler")
        val arguments = parser.getParsedArgs(args)

        val user = db.getUser(arguments.login.toString())



        if (arguments.isNeedHelp()) return HELP.exitCode

        if (arguments.isNeedAuthentication()) {
            val codeAuthentificate = authentificate(arguments.login.toString(), arguments.pass.toString())
            if (codeAuthentificate != SUCCESS.exitCode)
                return codeAuthentificate

        }
        if (arguments.isNeedAuthorization()) {
            val codeAuthorization = authorization(arguments.role!!, arguments.res!!, user!!.id!!)
            if (codeAuthorization != SUCCESS.exitCode)
                return codeAuthorization

        }
        if (arguments.isNeedAccounting()) {
            val activity = Activity(
                    role = Roles.valueOf(arguments.role.toString()),
                    res = arguments.res.toString(),
                    ds = arguments.ds.toString(),
                    de = arguments.de.toString(),
                    vol = arguments.vol.toString()
            )
            val codeAccoutig = accounting(activity)
            if (codeAccoutig != SUCCESS.exitCode)
                return codeAccoutig
        }




        return SUCCESS.exitCode
    }


}