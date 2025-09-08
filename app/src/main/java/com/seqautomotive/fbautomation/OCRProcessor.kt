package com.seqautomotive.fbautomation

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.util.regex.Pattern

class OCRProcessor {
    
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    suspend fun extractCarAndSellerInfo(bitmap: Bitmap): Pair<String?, String?> {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = textRecognizer.process(image).await()
            
            val extractedText = result.text
            val carModel = extractCarModel(extractedText)
            val sellerName = extractSellerName(extractedText)
            
            Pair(carModel, sellerName)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, null)
        }
    }
    
    private fun extractCarModel(text: String): String? {
        // Common car brands to look for
        val carBrands = listOf(
            "Toyota", "Honda", "Mazda", "Hyundai", "Kia", "Nissan", "Mitsubishi",
            "Subaru", "Ford", "Holden", "BMW", "Mercedes", "Audi", "Volkswagen",
            "Lexus", "Infiniti", "Acura", "Genesis", "Peugeot", "Renault",
            "Chevrolet", "Dodge", "Jeep", "Ram", "Chrysler", "Cadillac",
            "Buick", "GMC", "Lincoln", "Volvo", "Jaguar", "Land Rover",
            "Porsche", "Ferrari", "Lamborghini", "Maserati", "Alfa Romeo"
        )
        
        val lines = text.split("\n")
        
        // Look for car model in title-like lines (usually first few lines)
        for (i in 0 until minOf(5, lines.size)) {
            val line = lines[i].trim()
            
            // Skip year patterns and look for brand + model
            for (brand in carBrands) {
                if (line.contains(brand, ignoreCase = true)) {
                    // Extract brand and following word(s) as model
                    val pattern = Pattern.compile("(?i)\\b($brand)\\s+(\\w+)", Pattern.CASE_INSENSITIVE)
                    val matcher = pattern.matcher(line)
                    if (matcher.find()) {
                        return "${matcher.group(1)} ${matcher.group(2)}"
                    }
                }
            }
        }
        
        return null
    }
    
    private fun extractSellerName(text: String): String? {
        val lines = text.split("\n")
        
        // Look for seller name patterns
        for (line in lines) {
            val trimmedLine = line.trim()
            
            // Skip empty lines and lines with numbers/symbols
            if (trimmedLine.isEmpty() || 
                trimmedLine.contains("$") || 
                trimmedLine.contains("km") ||
                trimmedLine.matches(".*\\d{4}.*".toRegex()) || // Skip years
                trimmedLine.length < 3) {
                continue
            }
            
            // Look for name-like patterns (2-3 words, mostly letters)
            val namePattern = Pattern.compile("^[A-Za-z]+(?:\\s+[A-Za-z]+){0,2}$")
            if (namePattern.matcher(trimmedLine).matches()) {
                // Extract first name only
                return trimmedLine.split("\\s+".toRegex()).firstOrNull()
            }
        }
        
        return null
    }
}