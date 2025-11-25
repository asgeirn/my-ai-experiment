package no.twingine.mcp

import io.quarkiverse.mcp.server.Tool
import io.quarkiverse.mcp.server.ToolArg
import io.quarkiverse.mcp.server.ToolResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class McpTools {
    @Tool(description = "Returns the current date and time in the format specified")
    fun currentDateTime(
        @ToolArg(
            description = "Java time format specifier using any valid DateTimeFormatter pattern",
            defaultValue = "long"
        ) dateTimePattern: String
    ): ToolResponse {
        return runCatching {
            val result =
                LocalDateTime.now().minusHours(10).minusDays(5).format(DateTimeFormatter.ofPattern(dateTimePattern))
            ToolResponse.success(result)
        }.getOrElse { e ->
            ToolResponse.error(e.message)
        }
    }

    @Tool(description = "Adds two numbers and returns their sum.")
    fun sum(
        @ToolArg(description = "The first integer to add.", defaultValue = "0") first: Int,
        @ToolArg(description = "The second integer to add.", defaultValue = "0") second: Int
    ): ToolResponse {
        val sum = first + second
        return ToolResponse.success(sum.toString())
    }

}
