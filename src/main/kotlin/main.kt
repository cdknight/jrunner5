import tv.ramesh.JRunnerClientHandler
import tv.ramesh.ReflectionUtil;
import java.security.AllPermission
import java.security.BasicPermission
import java.lang.RuntimePermission
import java.security.Permission
import java.util.*
import java.net.*
import kotlin.concurrent.thread


fun main(args: Array<String>) {

    System.setSecurityManager(object : SecurityManager() {
        override fun checkPermission(perm: Permission) {
            if (perm is AllPermission || perm is BasicPermission) { // Literally block users from trying anything sneaky
                for (elem in Thread.currentThread().stackTrace) {
                    if (elem.className.startsWith("sandboxed")) {
                        throw SecurityException()
                    }
                }
            }
        }
    })

    val javaCode = """ // What if someone tries to import something? If they try importing jooR, they will literally get a SecurityException since reflection isn't allowed hehe
    public int xvceller() throws java.io.IOException {
        // Runtime.getRuntime().exec("firefox");
        System.out.println("RUnninghere");
        return 2;
    }
"""

    // val handler = JRunnerClientHandler()
    // handler.run()

    val port = 5791
    val server = ServerSocket(port);
    val handler = JRunnerClientHandler()
    println("DEBUG: Starting TCP socket server on port $port")

    while (true) {
        val client = server.accept()
        println("Found connection $client")

        thread {
            handler.run(client)
        }
    }
}

