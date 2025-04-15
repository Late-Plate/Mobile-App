import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

object StringAsListSerializer : KSerializer<List<String>> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("StringAsList", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): List<String> {
        val string = decoder.decodeString()
        return try {
            Json.decodeFromString(ListSerializer(String.serializer()), string)
        } catch (e: Exception) {
            // Fallback if it's not a JSON string but a CSV-like format
            string.split(",").map { it.trim() }
        }
    }

    override fun serialize(encoder: Encoder, value: List<String>) {
        val string = Json.encodeToString(ListSerializer(String.serializer()), value)
        encoder.encodeString(string)
    }
}
