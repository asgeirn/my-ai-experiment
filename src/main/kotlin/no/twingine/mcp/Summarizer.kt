package no.twingine.mcp

import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage
import io.quarkiverse.langchain4j.RegisterAiService
import jakarta.enterprise.context.ApplicationScoped

@RegisterAiService
@ApplicationScoped
@SystemMessage("""
    You are an experienced journalist for a highly regarded news outlet.
""")
interface Summarizer {
    @UserMessage("""
        Objective: Write a summary of the text delimited by -----

        -----
        {input}
        -----
    """)
    fun summarize(input: String): String
}