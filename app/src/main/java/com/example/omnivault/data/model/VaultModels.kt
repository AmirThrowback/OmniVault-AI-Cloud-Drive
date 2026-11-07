package com.example.omnivault.data.model

enum class FileCategory(val label: String) {
    ALL("All"),
    DOCUMENTS("Documents"),
    IMAGES("Photos"),
    NOTES("Notes"),
    VAULT("Vault Locked")
}

data class VaultFile(
    val id: String,
    val name: String,
    val sizeBytes: Long,
    val category: FileCategory,
    val mimeType: String,
    val isEncrypted: Boolean = false,
    val isVaultLocked: Boolean = false,
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val tags: List<String> = emptyList(),
    val analysisSummary: String? = null,
    val ocrText: String? = null,
    val documentType: String? = null,
    val safetyNote: String? = null,
    val keyDetails: List<String> = emptyList(),
    val audioGenerated: Boolean = false
) {
    val formattedSize: String
        get() = when {
            sizeBytes < 1024 -> "$sizeBytes B"
            sizeBytes < 1024 * 1024 -> "%.1f KB".format(sizeBytes / 1024.0)
            else -> "%.1f MB".format(sizeBytes / (1024.0 * 1024.0))
        }
}

data class StorageStats(
    val totalQuotaBytes: Long = 15L * 1024L * 1024L * 1024L, // 15 GB free tier
    val usedBytes: Long = 0L,
    val documentsBytes: Long = 0L,
    val imagesBytes: Long = 0L,
    val notesBytes: Long = 0L,
    val vaultBytes: Long = 0L
) {
    val percentageUsed: Float
        get() = if (totalQuotaBytes > 0) (usedBytes.toFloat() / totalQuotaBytes.toFloat()) * 100f else 0f

    val formattedUsed: String
        get() = when {
            usedBytes < 1024 * 1024 -> "%.1f KB".format(usedBytes / 1024.0)
            usedBytes < 1024 * 1024 * 1024 -> "%.1f MB".format(usedBytes / (1024.0 * 1024.0))
            else -> "%.2f GB".format(usedBytes / (1024.0 * 1024.0 * 1024.0))
        }
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val modelUsed: String = "gemini-3.8-flash"
)

data class StorageArchitecture(
    val id: String,
    val name: String,
    val provider: String,
    val badge: String,
    val description: String,
    val pros: List<String>,
    val cons: List<String>,
    val pricingModel: String,
    val bestFor: String,
    val technicalSpecs: String
)
