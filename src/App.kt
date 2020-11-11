import data.ExitCodes.*
import data.RoleResource
import data.Roles
import data.Activity
import services.DatabaseWrapper
import services.HandlerCLI
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

    //Проверка прошла аутентификация или нет
    private fun hasAuthentificate(login: String, pass: String): Boolean {
        when (authentificate(login, pass)) {
            2 -> return false
            3 -> return false
            4 -> return false
        }
        return true
    }

    //Проверка прошла авторизация или нет
    private fun hasAuthorization(roleStr: String, res: String, idUser: Long): Boolean {
        when (authorization(roleStr, res, idUser)) {
            5 -> return false
            6 -> return false
        }
        return true
    }
    private fun hasAccouting(activity: Activity):Boolean{
        when (accounting(activity)){
            7-> return false
        }
        return true
    }

    private fun authentificate(login: String, pass: String): Int {
        val user = db.getUser(login)
        when {
            isLoginValid(login) -> return INVALID_LOGIN_FORM.exitCode
            !user.isInvalidUser() -> return UNKNOWN_LOGIN.exitCode
        }
        return if (user.hashPassword == md5(md5(pass) + user.salt))
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

    private fun accounting(activity: Activity): Int{
        if (!activity.hasValidDate())
            return INCORRECT_ACTIVITY.exitCode
        db.addActivity(activity)

        return SUCCESS.exitCode
    }

    fun run(args: Array<String>): Int {

        val handlerCLI = HandlerCLI()
        val arguments = handlerCLI.parse(args)
        val user = db.getUser(arguments.login.toString())
        /* Проверка на пустоту и справку */
        val activity = Activity(
                role = arguments.role.toString(),
                res = arguments.res.toString(),
                ds = arguments.ds.toString(),
                de = arguments.de.toString(),
                vol = arguments.vol.toString()
        )

        if (arguments.isNeedHelp()) return HELP.exitCode

        if (arguments.isNeedAuthentication()) {
            if (!hasAuthentificate(arguments.login.toString(), arguments.pass.toString()))
                return authentificate(arguments.login.toString(), arguments.pass.toString())
        }
        if (arguments.isNeedAuthorization())
            if (!hasAuthorization(arguments.role!!, arguments.res!!, user.id!!))
                return authorization(arguments.role!!, arguments.res!!, user.id)

        if (arguments.isNeedAccounting())
            if (!hasAccouting(activity))
                return accounting(activity)



        return SUCCESS.exitCode
    }


}