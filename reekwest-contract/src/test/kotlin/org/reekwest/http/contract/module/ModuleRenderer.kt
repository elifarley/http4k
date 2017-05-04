package org.reekwest.http.contract.module

import org.reekwest.http.core.Response
import org.reekwest.http.lens.Failure

interface ModuleRenderer {
    fun notFound(): Response

    fun badRequest(failures: List<Failure>): Response

    fun description(moduleRoot: BasePath, security: Security, routes: List<ServerRoute>): Response
}
