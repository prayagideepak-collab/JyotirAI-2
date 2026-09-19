package com.example.engine

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiAiHelper {

    private val generativeModel: GenerativeModel by lazy {
        try {
            Firebase.ai.generativeModel("gemini-3.5-flash")
        } catch (e: Exception) {
            // Fallback or uninitialized model handler
            Firebase.ai.generativeModel("gemini-3.5-flash")
        }
    }

    suspend fun askAstrologer(prompt: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val systemInstruction = "You are an expert Vedic Astrologer (Jyotishi) with deep knowledge of Kundli, Dashas, Yogas, Nakshatras, and astrological remedies. Provide wise, compassionate, and culturally authentic answers in Hindi or English depending on user query."
                val fullPrompt = "$systemInstruction\n\nUser Question: $prompt"
                val response = generativeModel.generateContent(fullPrompt)
                response.text ?: "ज्योतिषीय गणना के अनुसार, आपका समय अनुकूल है। धैर्य और निष्ठा से आगे बढ़ें।"
            } catch (e: Exception) {
                // Graceful fallback when offline or API key is not configured
                getFallbackResponse(prompt)
            }
        }
    }

    suspend fun getDailyHoroscope(profileName: String, birthPlace: String, birthDate: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = "Provide a detailed daily astrological horoscope in Hindi for profile name: $profileName, born in $birthPlace on $birthDate. Include sections: 1. Overview (आज का राशिफल), 2. Career & Business (करियर एवं व्यापार), 3. Finance & Wealth (धन एवं आर्थिक स्थिति), 4. Health & Vitality (स्वास्थ्य), 5. Auspicious Color & Number (शुभ रंग एवं अंक), 6. Effective Remedy (विशेष उपाय)."
                val response = generativeModel.generateContent(prompt)
                response.text ?: getFallbackDailyHoroscope(profileName)
            } catch (e: Exception) {
                getFallbackDailyHoroscope(profileName)
            }
        }
    }

    private fun getFallbackDailyHoroscope(name: String): String {
        return "🌟 **$name के लिए आज का ज्योतिषीय फलादेश**:\n\n" +
                "• **आज का अवलोकन**: चंद्रमा गोचर आपकी राशि के अनुकूल है। मानसिक शांति बनी रहेगी और आत्मविश्वास मजबूत रहेगा।\n" +
                "• **करियर एवं व्यवसाय**: कार्यक्षेत्र में सहकर्मियों का सहयोग मिलेगा। नए प्रोजेक्ट्स की शुरुआत के लिए आज का दिन उत्तम है।\n" +
                "• **धन एवं आर्थिक स्थिति**: आय के नए स्रोत बनेंगे। अटके हुए धन की प्राप्ति हो सकती है।\n" +
                "• **स्वास्थ्य**: स्वास्थ्य उत्तम रहेगा। सुबह की सैर और योग से ऊर्जावान महसूस करेंगे।\n" +
                "• **शुभ रंग एवं अंक**: पीला रंग, अंक 3.\n" +
                "• **विशेष उपाय**: भगवान गणेश को दूर्वा अर्पित करें और 'ॐ गं गणपतये नमः' का 108 बार जाप करें।"
    }

    private fun getFallbackResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("शादी") || lower.contains("marriage") || lower.contains("विवाह") ->
                "सप्तम भाव में गुरु की दृष्टि से विवाह के लिए शुभ योग बन रहे हैं। मांगलिक दोष का परिहार कराकर आगे बढ़ें।"
            lower.contains("नौकरी") || lower.contains("job") || lower.contains("career") || lower.contains("करियर") ->
                "दशम भाव में सूर्य और बुध की युति 'बुधादित्य योग' बना रही है। करियर में प्रगति और मान-सम्मान प्राप्ति के प्रबल योग हैं।"
            lower.contains("धन") || lower.contains("wealth") || lower.contains("money") ->
                "द्वितीय और एकादश भाव के स्वामी की स्थिति धन लाभ की ओर इशारा कर रही है। गुरुवार को पीले वस्त्र पहनें।"
            else ->
                "नक्षत्रों की चाल आपके पक्ष में है। भगवान सूर्य को जल अर्पित करें और अपने इष्टदेव का स्मरण करें, सभी कार्य सफल होंगे।"
        }
    }
}
