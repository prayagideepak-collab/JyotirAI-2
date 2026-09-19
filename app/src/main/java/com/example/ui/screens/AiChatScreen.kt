package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GeminiAiHelper
import com.example.model.ChatMessage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    onBack: () -> Unit
) {
    var inputMessage by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    id = "1",
                    text = "नमस्ते! मैं आपका AI ज्योतिषी (AI Astrologer) हूँ। जन्म कुंडली, गोचर, दोष या भविष्य से जुड़ा कोई भी प्रश्न पूछें।",
                    isUser = false
                )
            )
        )
    }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val quickQuestions = listOf(
        "मेरी कुंडली में कौन सा योग है?",
        "करियर और जॉब में सफलता कब मिलेगी?",
        "वैवाहिक जीवन और मांगलिक दोष के उपाय",
        "आगामी वर्ष 2026-2027 का राशिफल"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("AI Astrologer Assistant", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Powered by Gemini 3.5 Flash", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(message = msg)
                }
                if (isLoading) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("ग्रहों की गणना और विश्लेषण हो रहा है...", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Quick Question Chips
            if (messages.size <= 2) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text("सुझाए गए प्रश्न:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
                    quickQuestions.forEach { question ->
                        OutlinedButton(
                            onClick = {
                                inputMessage = question
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(question, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        placeholder = { Text("ज्योतिष से संबंधित प्रश्न पूछें...") },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 50.dp),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputMessage.isNotBlank() && !isLoading) {
                                val userText = inputMessage
                                inputMessage = ""
                                val userMsg = ChatMessage(id = System.currentTimeMillis().toString(), text = userText, isUser = true)
                                messages = messages + userMsg
                                isLoading = true

                                coroutineScope.launch {
                                    val aiResponseText = GeminiAiHelper.askAstrologer(userText)
                                    val aiMsg = ChatMessage(id = (System.currentTimeMillis() + 1).toString(), text = aiResponseText, isUser = false)
                                    messages = messages + aiMsg
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Arrangement.End else Arrangement.Start
    val bubbleColor = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (message.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = bubbleColor,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                fontSize = 14.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
