package services

class HandlerCLI() {
    private val commands = arrayOf("-login", "-pass", "-res", "-role", "-ds", "-de", "-vol")

    fun run(args: Array<String>): MutableMap<String, String?> {
        val commandsArgs: MutableMap<String, String?> = commands.map { it to null }.toMap().toMutableMap()
        val sizeArgs = args.size
        when {
            args.isEmpty() -> terminate()
            args.contains("-h") -> terminate()
            sizeArgs !in arrayOf(4, 8, 14) -> terminate()
            sizeArgs != args.distinct().size -> terminate()
        }
        for (i in 0 until sizeArgs step 2) {
            val arg = args[i]
            if (arg !in commands)
                terminate()
            commandsArgs[arg] = args[i + 1]
        }
        return commandsArgs
    }
}