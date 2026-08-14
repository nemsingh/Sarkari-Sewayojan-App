package com.example.util

import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

data class ExtractedDates(
    val startDate: String? = null,
    val lastDate: String? = null
)

object HtmlDateExtractor {

    fun cleanText(str: String?): String {
        if (str.isNullOrBlank()) return ""
        return str
            .replace(Regex("[\\u00a0\\u1680\\u2000-\\u200a\\u2028\\u2029\\u202f\\u205f\\u3000\\ufeff]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun extractDateText(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val cleaned = cleanText(text)

        val finalVal = cleaned
            .replace(Regex("^[:\\-–—\\s\\u200b•|ः।]+"), "")
            .replace(Regex("[:\\-–—\\s|ः।]+$"), "")
            .trim()

        if (finalVal.isBlank()) return null

        val monthPattern = Regex("(?i)\\b(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec|जनवरी|फ़रवरी|फरवरी|मार्च|अप्रैल|मई|जून|जुलाई|अगस्त|सितंबर|सितम्बर|अक्टूबर|अक्तूबर|नवंबर|नवम्बर|दिसंबर|दिसम्बर)")
        val numericPattern = Regex("(?i)\\b\\d{1,2}[./-]\\d{1,2}[./-]\\d{2,4}\\b")

        val hasDate = numericPattern.containsMatchIn(finalVal) || monthPattern.containsMatchIn(finalVal)

        if (hasDate && finalVal.length < 55) {
            return finalVal
        }

        // Regex 1: Numeric Date
        val matchNumeric = numericPattern.find(finalVal)
        if (matchNumeric != null) return matchNumeric.value

        // Regex 2: Alpha Date
        val matchAlpha = Regex("(?i)\\b\\d{1,2}(?:st|nd|rd|th)?[\\s./-]*(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec|जनवरी|फ़रवरी|फरवरी|मार्च|अप्रैल|मई|जून|जुलाई|अगस्त|सितंबर|सितम्बर|अक्टूबर|अक्तूबर|नवंबर|नवम्बर|दिसंबर|दिसम्बर)[a-z]*[\\s./-]*\\d{2,4}\\b").find(finalVal)
        if (matchAlpha != null) return matchAlpha.value

        // Regex 3: Reverse Alpha Date
        val matchAlphaRev = Regex("(?i)\\b(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec|जनवरी|फ़रवरी|फरवरी|मार्च|अप्रैल|मई|जून|जुलाई|अगस्त|सितंबर|सितम्बर|अक्टूबर|अक्तूबर|नवंबर|नवम्बर|दिसंबर|दिसम्बर)[a-z]*[\\s./-]*\\d{1,2}(?:st|nd|rd|th)?[\\s./-]*\\d{2,4}\\b").find(finalVal)
        if (matchAlphaRev != null) return matchAlphaRev.value

        if (finalVal.length < 25 && finalVal.any { it.isDigit() }) {
            return finalVal
        }

        return null
    }

    private fun isElementRed(el: Element): Boolean {
        val inlineStyle = el.attr("style").lowercase()
        val colorAttr = el.attr("color").lowercase()
        val classAttr = el.attr("class").lowercase()

        if (inlineStyle.contains("color: red") ||
            inlineStyle.contains("color:red") ||
            colorAttr == "red" ||
            classAttr.contains("text-red") ||
            classAttr.contains("red-text") ||
            inlineStyle.contains("rgb(255, 0, 0)") ||
            inlineStyle.contains("rgb(255,0,0)")
        ) {
            return true
        }

        val styleColorMatch = Regex("color\\s*:\\s*(#[0-9a-f]{3,8}|rgb\\([^)]+\\)|rgba\\([^)]+\\))").find(inlineStyle)
        val colorVal = styleColorMatch?.groupValues?.get(1) ?: if (colorAttr.startsWith("#") || colorAttr.startsWith("rgb")) colorAttr else null

        if (colorVal != null && colorVal.startsWith("#")) {
            val hex = colorVal.substring(1)
            try {
                if (hex.length == 3 || hex.length == 4) {
                    val r = hex.substring(0, 1).toInt(16)
                    val g = hex.substring(1, 2).toInt(16)
                    val b = hex.substring(2, 3).toInt(16)
                    if (r >= 10 && r > g + 2 && r > b + 2) return true
                } else if (hex.length >= 6) {
                    val r = hex.substring(0, 2).toInt(16)
                    val g = hex.substring(2, 4).toInt(16)
                    val b = hex.substring(4, 6).toInt(16)
                    if (r >= 120 && r > g + 35 && r > b + 35) return true
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
        return false
    }

    private fun hasRedAncestorOrSelf(element: Element): Boolean {
        var curr: Element? = element
        while (curr != null) {
            if (isElementRed(curr)) return true
            if (curr.tagName().equals("body", ignoreCase = true)) break
            curr = curr.parent()
        }
        return false
    }

    private fun isElementBold(el: Element): Boolean {
        val tag = el.tagName().lowercase()
        if (tag == "b" || tag == "strong" || tag == "th") return true
        val inlineStyle = el.attr("style").lowercase()
        val classAttr = el.attr("class").lowercase()
        return inlineStyle.contains("font-weight: bold") ||
                inlineStyle.contains("font-weight:700") ||
                classAttr.contains("font-bold")
    }

    private fun hasBoldAncestorOrSelf(element: Element): Boolean {
        var curr: Element? = element
        while (curr != null) {
            if (isElementBold(curr)) return true
            if (curr.tagName().equals("body", ignoreCase = true)) break
            curr = curr.parent()
        }
        return false
    }

    fun extractDatesFromHtml(htmlContent: String?): ExtractedDates {
        if (htmlContent.isNullOrBlank()) return ExtractedDates(null, null)

        try {
            val doc = Jsoup.parse(htmlContent)

            var startDate: String? = null
            var lastDate: String? = null

            val startKeywords = listOf(
                Regex("(?i)apply\\s*online\\s*(?:start|begin|date)"),
                Regex("(?i)application\\s*(?:begin|start|commencement|open|from|opening)"),
                Regex("(?i)registration\\s*(?:begin|start|date|open|from)"),
                Regex("(?i)starting\\s*date"),
                Regex("(?i)form\\s*(?:begin|start|from|open)"),
                Regex("(?i)apply\\s*(?:date|start|begin|from)"),
                Regex("शुरुआती\\s*तारीख"),
                Regex("प्रारंभिक\\s*तिथि"),
                Regex("प्रारम्भिक\\s*तिथि"),
                Regex("आवेदन\\s*(?:शुरू|प्रारंभ|आरंभ|तिथि|शुरु|प्रारम्भ)"),
                Regex("रजिस्ट्रेशन\\s*(?:शुरू|प्रारंभ|आरंभ|तिथि|शुरु|प्रारम्भ)"),
                Regex("शुरू\\s*होने\\s*की\\s*तिथि")
            )

            val lastKeywords = listOf(
                Regex("(?i)last\\s*date\\s*for\\s*(?:apply|online|registration|submission|form|applying|submit)"),
                Regex("(?i)last\\s*date\\s*to\\s*(?:apply|register|submit|fill|complete)"),
                Regex("(?i)online\\s*application\\s*(?:end|close|last|expiry)"),
                Regex("(?i)registration\\s*(?:last|end|close)\\s*date"),
                Regex("(?i)apply\\s*last\\s*date"),
                Regex("(?i)application\\s*last\\s*date"),
                Regex("(?i)last\\s*date"),
                Regex("(?i)closing\\s*date"),
                Regex("(?i)end\\s*date"),
                Regex("(?i)expiry\\s*date"),
                Regex("आवेदन\\s*की\\s*(?:अंतिम|अन्तिम|आखिरी|आखरी)\\s*तिथि"),
                Regex("रजिस्ट्रेशन\\s*की\\s*(?:अंतिम|अन्तिम|आखिरी|आखरी)\\s*तिथि"),
                Regex("(?:अंतिम|अन्तिम|आखिरी|आखरी)\\s*तिथि"),
                Regex("(?:अंतिम|अन्तिम|आखिरी|आखरी)\\s*तारीख"),
                Regex("समाप्ति\\s*तिथि")
            )

            // LAYER 1: Scan HTML Table Rows (<tr><td>Key</td><td>Date</td></tr>)
            val rows = doc.select("tr")
            for (row in rows) {
                val cells = row.select("td, th")
                if (cells.size >= 2) {
                    for (i in 0 until cells.size) {
                        val cellText = cleanText(cells[i].text())
                        if (cellText.isBlank()) continue

                        // Check Start Date
                        if (startDate == null && startKeywords.any { it.containsMatchIn(cellText) }) {
                            for (j in i + 1 until cells.size) {
                                val nextText = cleanText(cells[j].text())
                                val extracted = extractDateText(nextText)
                                if (extracted != null) {
                                    startDate = extracted
                                    break
                                }
                            }
                        }

                        // Check Last Date
                        if (lastDate == null && lastKeywords.any { it.containsMatchIn(cellText) }) {
                            for (j in i + 1 until cells.size) {
                                val nextText = cleanText(cells[j].text())
                                val extracted = extractDateText(nextText)
                                if (extracted != null) {
                                    lastDate = extracted
                                    break
                                }
                            }
                        }
                    }
                }
            }

            // LAYER 2: Scan Paragraphs, Divs, Spans for Inline Keywords
            if (lastDate == null || startDate == null) {
                val elements = doc.select("td, th, p, li, span, div, b, strong")
                for (el in elements) {
                    val text = cleanText(el.text())
                    if (text.isBlank() || text.length > 200) continue

                    if (lastDate == null) {
                        for (kw in lastKeywords) {
                            val matchKw = kw.find(text)
                            if (matchKw != null) {
                                val index = text.lowercase().indexOf(matchKw.value.lowercase())
                                if (index >= 0) {
                                    val afterText = text.substring(index + matchKw.value.length)
                                    val extracted = extractDateText(afterText)
                                    if (extracted != null) {
                                        lastDate = extracted
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // FALLBACK LAYER 1: Red + Bold Elements Search
            if (lastDate == null) {
                val allDocElements = doc.select("td, th, p, li, span, div, b, strong, font")
                for (el in allDocElements) {
                    val text = cleanText(el.text())
                    if (text.length in 6..79) {
                        val dateMatch = extractDateText(text)
                        if (dateMatch != null) {
                            val lowerText = text.lowercase()
                            val hasStartKeyword = Regex("(?i)start|begin|शुरू|प्रारंभ").containsMatchIn(lowerText)
                            if (!hasStartKeyword && hasRedAncestorOrSelf(el) && hasBoldAncestorOrSelf(el)) {
                                lastDate = dateMatch
                                break
                            }
                        }
                    }
                }
            }

            // FALLBACK LAYER 2: Any Red Element with a Valid Date
            if (lastDate == null) {
                val allDocElements = doc.select("td, th, p, li, span, div, b, strong, font")
                for (el in allDocElements) {
                    val text = cleanText(el.text())
                    if (text.length in 6..79) {
                        val dateMatch = extractDateText(text)
                        if (dateMatch != null) {
                            val lowerText = text.lowercase()
                            val hasStartKeyword = Regex("(?i)start|begin|शुरू|प्रारंभ").containsMatchIn(lowerText)
                            if (!hasStartKeyword && hasRedAncestorOrSelf(el)) {
                                lastDate = dateMatch
                                break
                            }
                        }
                    }
                }
            }

            return ExtractedDates(startDate, lastDate)
        } catch (err: Exception) {
            Log.e("HtmlDateExtractor", "Error extracting dates from HTML: ${err.message}")
            return ExtractedDates(null, null)
        }
    }
}
