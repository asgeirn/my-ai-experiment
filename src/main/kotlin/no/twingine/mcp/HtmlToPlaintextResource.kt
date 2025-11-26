package no.twingine.mcp

import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

/**
 * REST resource for converting HTML to plaintext.
 */
@Path("/api/plaintext")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class HtmlToPlaintextResource {

    @Inject
    lateinit var htmlToPlaintextService: HtmlToPlaintextService

    /**
     * Extract plaintext from a URL.
     *
     * Example: POST /api/plaintext/extract
     * Body: {"url": "https://example.com"}
     */
    @POST
    @Path("/extract")
    fun extractFromUrl(request: UrlRequest): Response {
        return try {
            val plaintext = htmlToPlaintextService.extractPlaintext(
                url = request.url,
                timeout = request.timeout ?: 30000,
                preserveLineBreaks = request.preserveLineBreaks ?: true
            )
            Response.ok(PlaintextResponse(plaintext = plaintext)).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(ErrorResponse(e.message ?: "Invalid request"))
                .build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorResponse("Failed to extract plaintext: ${e.message}"))
                .build()
        }
    }

    /**
     * Extract plaintext with metadata from a URL.
     *
     * Example: POST /api/plaintext/extract-with-metadata
     * Body: {"url": "https://example.com"}
     */
    @POST
    @Path("/extract-with-metadata")
    fun extractWithMetadata(request: UrlRequest): Response {
        return try {
            val result = htmlToPlaintextService.extractPlaintextWithMetadata(
                url = request.url,
                timeout = request.timeout ?: 30000
            )
            Response.ok(result).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(ErrorResponse(e.message ?: "Invalid request"))
                .build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorResponse("Failed to extract plaintext: ${e.message}"))
                .build()
        }
    }

    /**
     * Extract plaintext from provided HTML content.
     *
     * Example: POST /api/plaintext/parse
     * Body: {"html": "<html><body>Hello World</body></html>"}
     */
    @POST
    @Path("/parse")
    fun parseHtml(request: HtmlRequest): Response {
        return try {
            val plaintext = htmlToPlaintextService.extractPlaintextFromHtml(
                html = request.html,
                preserveLineBreaks = request.preserveLineBreaks ?: true
            )
            Response.ok(PlaintextResponse(plaintext = plaintext)).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorResponse("Failed to parse HTML: ${e.message}"))
                .build()
        }
    }

    /**
     * Health check endpoint.
     */
    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    fun health(): String {
        return "HTML to Plaintext service is running"
    }

    data class UrlRequest(
        val url: String,
        val timeout: Int? = null,
        val preserveLineBreaks: Boolean? = null
    )

    data class HtmlRequest(
        val html: String,
        val preserveLineBreaks: Boolean? = null
    )

    data class PlaintextResponse(
        val plaintext: String
    )

    data class ErrorResponse(
        val error: String
    )
}
