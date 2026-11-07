package com.example.omnivault.data.repository

import com.example.omnivault.data.model.FileCategory
import com.example.omnivault.data.model.StorageArchitecture
import com.example.omnivault.data.model.StorageStats
import com.example.omnivault.data.model.VaultFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class VaultRepository {

    private val _files = MutableStateFlow<List<VaultFile>>(getInitialSampleFiles())
    val files: StateFlow<List<VaultFile>> = _files.asStateFlow()

    private val _isVaultUnlocked = MutableStateFlow(false)
    val isVaultUnlocked: StateFlow<Boolean> = _isVaultUnlocked.asStateFlow()

    private var masterPin = "1234"

    fun verifyPin(pin: String): Boolean {
        val valid = (pin == masterPin)
        if (valid) {
            _isVaultUnlocked.value = true
        }
        return valid
    }

    fun lockVault() {
        _isVaultUnlocked.value = false
    }

    fun addFile(
        name: String,
        content: String,
        category: FileCategory,
        mimeType: String = "text/plain",
        isEncrypted: Boolean = false,
        tags: List<String> = emptyList(),
        sizeBytes: Long = (content.toByteArray().size.toLong()).coerceAtLeast(1024L)
    ): VaultFile {
        val newFile = VaultFile(
            id = UUID.randomUUID().toString(),
            name = name,
            sizeBytes = sizeBytes,
            category = category,
            mimeType = mimeType,
            isEncrypted = isEncrypted,
            isVaultLocked = isEncrypted,
            content = content,
            tags = tags
        )
        _files.value = listOf(newFile) + _files.value
        return newFile
    }

    fun updateFile(file: VaultFile) {
        _files.value = _files.value.map { if (it.id == file.id) file else it }
    }

    fun deleteFile(fileId: String) {
        _files.value = _files.value.filter { it.id != fileId }
    }

    fun calculateStats(): StorageStats {
        val currentFiles = _files.value
        val used = currentFiles.sumOf { it.sizeBytes }
        val docs = currentFiles.filter { it.category == FileCategory.DOCUMENTS }.sumOf { it.sizeBytes }
        val images = currentFiles.filter { it.category == FileCategory.IMAGES }.sumOf { it.sizeBytes }
        val notes = currentFiles.filter { it.category == FileCategory.NOTES }.sumOf { it.sizeBytes }
        val vault = currentFiles.filter { it.isEncrypted }.sumOf { it.sizeBytes }

        return StorageStats(
            usedBytes = used,
            documentsBytes = docs,
            imagesBytes = images,
            notesBytes = notes,
            vaultBytes = vault
        )
    }

    fun getArchitectures(): List<StorageArchitecture> = listOf(
        StorageArchitecture(
            id = "cloudflare-r2",
            name = "Cloudflare R2 Object Storage",
            provider = "Cloudflare",
            badge = "Zero Egress Fees",
            description = "S3-compatible object storage designed with zero bandwidth/egress egress fees. Ideal for personal cloud drives, streaming multimedia, and encrypted backup archives.",
            pros = listOf(
                "No egress bandwidth fees ($0/GB transfer out)",
                "Full S3 API compatibility with existing clients",
                "10 GB monthly free storage tier included",
                "Global low-latency Anycast CDN integration"
            ),
            cons = listOf(
                "Requires custom authorization token generation",
                "Fewer enterprise compliance certifications than AWS"
            ),
            pricingModel = "$0.015 / GB-month | $0 Egress",
            bestFor = "Personal drives, media backups, mobile app sync",
            technicalSpecs = "API: S3-Compatible | SLA: 99.9% | Replication: Multi-region distributed"
        ),
        StorageArchitecture(
            id = "aws-s3",
            name = "Amazon Web Services S3",
            provider = "Amazon Web Services",
            badge = "Industry Standard",
            description = "The gold standard for object storage offering 99.999999999% (11 9s) durability, intelligent tiering, lifecycle archival to Glacier, and granular IAM permission policies.",
            pros = listOf(
                "11 9s of durability across multiple availability zones",
                "Glacier & Deep Archive tier for long-term retention",
                "Deep ecosystem with thousands of third-party tools"
            ),
            cons = listOf(
                "Significant egress data transfer fees (~$0.09/GB)",
                "Complex pricing and permission configuration"
            ),
            pricingModel = "$0.023 / GB-month + Egress fees",
            bestFor = "Mission-critical enterprise data & deep archives",
            technicalSpecs = "Durability: 99.999999999% | Auth: AWS IAM / SigV4 | Encryption: SSE-KMS"
        ),
        StorageArchitecture(
            id = "self-hosted-nextcloud",
            name = "Nextcloud / MinIO Self-Hosted",
            provider = "Self-Hosted / HomeLab",
            badge = "100% Privacy",
            description = "Complete self-sovereignty by hosting your own cloud storage node on a Home Server, Raspberry Pi 5, or private VPS with zero third-party telemetry.",
            pros = listOf(
                "100% ownership and zero third-party access",
                "Built-in file sync, calendar, notes, and contacts",
                "No recurring subscription or quota limits"
            ),
            cons = listOf(
                "Requires personal hardware maintenance and backup setup",
                "Home internet upload speed limits download rate"
            ),
            pricingModel = "Hardware cost only ($0 recurring)",
            bestFor = "Privacy enthusiasts, homelabs, offline access",
            technicalSpecs = "Stack: Docker / TrueNAS / Linux | Protocol: WebDAV & S3 | Encryption: LUKS + Nextcloud Server-Side"
        ),
        StorageArchitecture(
            id = "supabase-storage",
            name = "Supabase / Firebase Cloud Storage",
            provider = "Supabase / Google Cloud",
            badge = "Developer Friendly",
            description = "Modern object storage integrated directly with PostgreSQL database and authentication. Provides Row-Level Security policies to control per-user folder access.",
            pros = listOf(
                "Native authentication and Row Level Security (RLS)",
                "Automatic image resizing and WebP transformation",
                "Generous free tier (1GB - 5GB)"
            ),
            cons = listOf(
                "Tied to application database infrastructure",
                "Not designed for massive multi-terabyte bulk archives"
            ),
            pricingModel = "Included in project tier / $0.021 per GB",
            bestFor = "Mobile and web application backend data storage",
            technicalSpecs = "Database: PostgreSQL | Security: Row-Level Security (RLS) | CDN: Fastly"
        ),
        StorageArchitecture(
            id = "zero-knowledge-vault",
            name = "Client-Side Zero-Knowledge Vault",
            provider = "OmniVault Security Engine",
            badge = "Maximum Security",
            description = "Client-side cryptographic envelope. Every byte is encrypted with AES-256-GCM using keys derived from your PIN on-device before hitting any cloud provider.",
            pros = listOf(
                "Cloud host or eavesdropper cannot read file content",
                "Authenticated encryption guarantees tampering detection",
                "Works transparently on top of any storage backend (R2, S3, Drive)"
            ),
            cons = listOf(
                "Losing your Master PIN means permanent data loss",
                "Server-side full-text search requires encrypted indexing"
            ),
            pricingModel = "Built into OmniVault (Free)",
            bestFor = "Passports, identity cards, tax forms, private keys",
            technicalSpecs = "Cipher: AES-256-GCM | KDF: PBKDF2 / Argon2id | Nonce: 96-bit CSPRNG"
        )
    )

    private fun getInitialSampleFiles(): List<VaultFile> = listOf(
        VaultFile(
            id = "file-001",
            name = "national_passport_scan.pdf",
            sizeBytes = 2_840_000L,
            category = FileCategory.DOCUMENTS,
            mimeType = "application/pdf",
            isEncrypted = true,
            isVaultLocked = true,
            content = "GOVERNMENT OF INDIA / PASSPORT / NATIONAL IDENTITY DOCUMENT\nDoc Type: P | Country: IND | Code: IND\nSurname: THROWBACK | Given Names: AMIR\nNationality: INDIAN | Sex: M | Date of Birth: 18 MAR 1994\nPlace of Issue: NEW DELHI | Valid Until: 2034\nAuthority: PASSPORT OFFICE REGIONAL",
            tags = listOf("identity", "passport", "official", "vault-priority"),
            documentType = "Government Identification",
            analysisSummary = "National passport scan containing personal identification credentials, biometric zone data, and validity dates.",
            ocrText = "PASSPORT P<INDTHROWBACK<<AMIR<<<<<<<<<<<<<<<<<<<\nZ94821034IND9403184M3408124<<<<<<<<<<<<<<04",
            safetyNote = "⚠️ Critical Sensitivity: Government ID document. Protected under AES-256 encrypted vault.",
            keyDetails = listOf("Document: Passport", "Expiry: 2034", "Security: Level 5 Locked")
        ),
        VaultFile(
            id = "file-002",
            name = "cloud_architecture_strategy_2026.md",
            sizeBytes = 48_500L,
            category = FileCategory.NOTES,
            mimeType = "text/markdown",
            isEncrypted = false,
            content = "# Cloud Storage Architecture Plan\n\nTarget: Store 1TB+ personal multimedia and sensitive documents.\n\nStrategy:\n1. Cloudflare R2 for zero egress bandwidth.\n2. Client-side AES-256 encryption via Android Keystore.\n3. Automatic OCR with Gemini visual intelligence for receipts & invoices.\n4. Text-to-Speech narration for reading long strategy docs on the go.",
            tags = listOf("architecture", "r2", "strategy", "gemini"),
            documentType = "Source Note / Documentation",
            analysisSummary = "Comprehensive personal cloud storage roadmap comparing S3 egress costs with Cloudflare R2 and client-side encryption.",
            ocrText = "Cloud Storage Architecture Plan - Target: Store 1TB+ personal multimedia...",
            safetyNote = "✅ Standard Sensitivity: Engineering documentation.",
            keyDetails = listOf("Topic: Storage Arch", "Author: Amir", "Updated: 2026")
        ),
        VaultFile(
            id = "file-003",
            name = "cloud_server_hardware_invoice.pdf",
            sizeBytes = 380_000L,
            category = FileCategory.DOCUMENTS,
            mimeType = "application/pdf",
            isEncrypted = false,
            content = "TAX INVOICE / HARDWARE SUPPLY\nOrder #NV-882910 | Date: 12-OCT-2026\nItem: 4TB NVMe Enterprise Storage Node (U.2 PCIe Gen5)\nSubtotal: $420.00\nSales Tax (8.25%): $34.65\nTotal Paid: $454.65 via Corporate Card ending 4419",
            tags = listOf("finance", "hardware", "invoice", "tax"),
            documentType = "Commercial Invoice",
            analysisSummary = "Hardware equipment purchase invoice for 4TB NVMe enterprise storage node.",
            ocrText = "TAX INVOICE - Total Paid: $454.65 via Corporate Card ending 4419",
            safetyNote = "🔒 Moderate Sensitivity: Financial transaction invoice with payment records.",
            keyDetails = listOf("Total: $454.65", "Vendor: Enterprise Storage Co", "Tax Deductible: Yes")
        ),
        VaultFile(
            id = "file-004",
            name = "family_monsoon_retreat.jpg",
            sizeBytes = 4_120_000L,
            category = FileCategory.IMAGES,
            mimeType = "image/jpeg",
            isEncrypted = false,
            content = "High dynamic range landscape photograph taken in Himachal hills during rainy season.",
            tags = listOf("family", "travel", "hills", "photo"),
            documentType = "High-Resolution Photograph",
            analysisSummary = "Lush green valley landscape during monsoon season with cloud-shrouded mountain peaks.",
            ocrText = "No prominent text detected in visual scenery.",
            safetyNote = "✅ Non-sensitive personal photograph.",
            keyDetails = listOf("Resolution: 4K HDR", "Location: Himachal Pradesh", "Format: JPEG")
        ),
        VaultFile(
            id = "file-005",
            name = "crypto_recovery_seed_backup.key",
            sizeBytes = 12_400L,
            category = FileCategory.NOTES,
            mimeType = "text/plain",
            isEncrypted = true,
            isVaultLocked = true,
            content = "VAULT PROTECTED CIPHERTEXT\n[AES-256-GCM::IV_7891AF::TAG_CC018E]\nMaster recovery seeds for primary multi-sig hardware wallet.\nStored under zero-knowledge vault protection.",
            tags = listOf("crypto", "security", "backup", "vault-priority"),
            documentType = "Cryptographic Key Recovery",
            analysisSummary = "Zero-knowledge encrypted key backup file. Requires PIN authorization to decrypt.",
            ocrText = "VAULT PROTECTED CIPHERTEXT - ENCRYPTED",
            safetyNote = "⚠️ Maximum Sensitivity: Cryptographic key material.",
            keyDetails = listOf("Cipher: AES-256-GCM", "Hardware Bound: True", "Lock State: Armed")
        )
    )
}
