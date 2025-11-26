package no.twingine.mcp

import jakarta.enterprise.context.ApplicationScoped
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URI

/**
 * Service for retrieving HTML content from URLs and extracting plaintext.
 */
@ApplicationScoped
class HtmlToPlaintextService {

    /**
     * Fetches HTML content from the given URL and extracts plaintext.
     *
     * @param url The URL of the web page to retrieve
     * @param timeout Connection timeout in milliseconds (default: 30000ms)
     * @param preserveLineBreaks Whether to preserve line breaks in the output (default: true)
     * @return The extracted plaintext content
     * @throws IllegalArgumentException if the URL is invalid
     * @throws java.io.IOException if there's an error fetching the content
     */
    fun extractPlaintext(
        url: String,
        timeout: Int = 30000,
        preserveLineBreaks: Boolean = true
    ): String {
        // Validate URL
        validateUrl(url)

        // Fetch the HTML document
        val document = fetchDocument(url, timeout)

        // Extract plaintext
        return extractTextFromDocument(document, preserveLineBreaks)
    }

    /**
     * Extracts plaintext from already fetched HTML content.
     *
     * @param html The HTML content as a string
     * @param preserveLineBreaks Whether to preserve line breaks in the output (default: true)
     * @return The extracted plaintext content
     */
    fun extractPlaintextFromHtml(
        html: String,
        preserveLineBreaks: Boolean = true
    ): String {
        val document = Jsoup.parse(html)
        return extractTextFromDocument(document, preserveLineBreaks)
    }

    private fun validateUrl(url: String) {
        require(url.isNotBlank()) { "URL cannot be blank" }
        runCatching {
            URI(url).toURL()
        }.getOrElse {
            throw IllegalArgumentException("Invalid URL format: $url", it)
        }
    }

    private fun fetchDocument(url: String, timeout: Int): Document {
        return Jsoup.connect(url)
            .timeout(timeout)
            .userAgent("Mozilla/5.0 (compatible; HtmlToPlaintextService/1.0)")
            .followRedirects(true)
            .get()
    }

    private fun extractTextFromDocument(document: Document, preserveLineBreaks: Boolean): String {
        // Remove script and style elements
        document.select("script, style, nav, footer, aside").remove()

        // Extract text - wholeText() preserves line breaks, text() removes them
        val text = if (preserveLineBreaks) {
            document.body().wholeText()
        } else {
            document.body().text()
        }

        // Clean up the text
        return cleanText(text, preserveLineBreaks)
    }

    private fun cleanText(text: String, preserveLineBreaks: Boolean): String {
        return if (preserveLineBreaks) {
            var cleaned = text.trim()
                .replace(Regex("[ \\t]{2,}"), " ") // Replace multiple spaces/tabs with single space (but not newlines)

            // Keep replacing 3+ newlines (with possible spaces/tabs between them) with double newlines
            // until no more replacements are possible
            var previous: String
            do {
                previous = cleaned
                // Match newlines with optional spaces/tabs between them
                cleaned = cleaned.replace(Regex("\\n[ \\t]*\\n[ \\t]*\\n([ \\t]*\\n)*"), "\n\n")
            } while (cleaned != previous)

            cleaned
        } else {
            text
                .trim()
                .replace(Regex("\\s+"), " ") // Replace all whitespace (including newlines) with single space
        }
    }

    /**
     * Extracts plaintext with additional metadata.
     *
     * @param url The URL of the web page to retrieve
     * @param timeout Connection timeout in milliseconds (default: 30000ms)
     * @return PlaintextResult containing the text and metadata
     */
    fun extractPlaintextWithMetadata(
        url: String,
        timeout: Int = 30000
    ): PlaintextResult {
        validateUrl(url)
        val document = fetchDocument(url, timeout)

        val title = document.title()
        val description = document.select("meta[name=description]").attr("content")
        val text = extractTextFromDocument(document, true)

        return PlaintextResult(
            url = url,
            title = title,
            description = description,
            plaintext = text,
            contentLength = text.length
        )
    }

    data class PlaintextResult(
        val url: String,
        val title: String,
        val description: String,
        val plaintext: String,
        val contentLength: Int
    )
}
