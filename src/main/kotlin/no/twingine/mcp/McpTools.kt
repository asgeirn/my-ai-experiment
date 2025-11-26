package no.twingine.mcp

import io.quarkiverse.mcp.server.Tool
import io.quarkiverse.mcp.server.ToolArg
import io.quarkiverse.mcp.server.ToolResponse
import jakarta.inject.Inject
import org.jboss.logging.Logger

class McpTools {
    private val log = Logger.getLogger(McpTools::class.java)

    @Inject
    lateinit var summarizer: Summarizer

    @Inject
    lateinit var htmlToPlaintextService: HtmlToPlaintextService

    @Tool(description = "Summarizes the provided text, extracting key points")
    fun summarize(
        @ToolArg(description = "The text to summarize, in Markdown format") text: String
    ): ToolResponse {
        return runCatching {
            log.debugf("Summarizing the following text:  <<<%s>>>", text)
            ToolResponse.success(summarizer.summarize(text))
        }.getOrElse { e ->
            log.error("Failed to summarize text", e)
            ToolResponse.error(e.message)
        }
    }

    @Tool(description = "Extracts plaintext content directly from a URL by fetching and parsing the HTML")
    fun extractPlaintextFromUrl(
        @ToolArg(description = "URL of the webpage to extract plaintext from") url: String,
        @ToolArg(description = "Connection timeout in milliseconds", defaultValue = "30000") timeout: Int,
        @ToolArg(
            description = "Whether to preserve line breaks in the output. true: keeps paragraph structure with newlines, false: creates continuous text with single spaces",
            defaultValue = "true"
        ) preserveLineBreaks: Boolean
    ): ToolResponse {
        return runCatching {
            val plaintext = htmlToPlaintextService.extractPlaintext(url, timeout, preserveLineBreaks)
            ToolResponse.success(plaintext)
        }.getOrElse { e ->
            log.error("Failed to extract plaintext from URL: $url", e)
            ToolResponse.error(e.message ?: "Failed to extract plaintext from URL")
        }
    }
}
