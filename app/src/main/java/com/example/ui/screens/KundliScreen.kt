package com.example.ui.screens

import android.content.Intent
import android.graphics.Color as GColor
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.ProfileManager
import com.example.ui.theme.SaffronPrimary
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KundliScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val view = LocalView.current
    val profileManager = remember { ProfileManager.getInstance(context) }
    val activeProfile by profileManager.activeProfileFlow.collectAsState(initial = null)

    if (activeProfile?.name.equals("Example", ignoreCase = true)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("जन्म कुंडली (Kundli)") },
                    navigationIcon = {
                        IconButton(onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                            onBack()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Example", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = SaffronPrimary)
                        Text(
                            text = "यह केवल उदाहरण के लिए है। इस प्रोफाइल पर कोई व्यक्तिगत गणना नहीं होगी।",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("जन्म कुंडली (Lagna)", "नवमांश (D9)", "ग्रह स्थिति (Planets)")

    fun exportPdf() {
        try {
            val pdfDoc = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint().apply {
                color = GColor.BLACK
                textSize = 14f
            }
            val titlePaint = Paint().apply {
                color = GColor.rgb(220, 100, 0)
                textSize = 20f
                isFakeBoldText = true
            }

            canvas.drawText("JyotirAI - Vedic Birth Kundli Report", 50f, 60f, titlePaint)
            paint.textSize = 12f
            canvas.drawText("Profile: ${activeProfile?.name ?: "साधक"} (DOB: ${activeProfile?.birthDate ?: "–"})", 50f, 90f, paint)
            canvas.drawText("Birth Place: ${activeProfile?.birthPlace ?: "New Delhi"} | Time: ${activeProfile?.birthTime ?: "10:30 AM"}", 50f, 115f, paint)
            canvas.drawText("Generated on: " + SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()), 50f, 140f, paint)
            canvas.drawText("------------------------------------------------------------------------------------------------", 50f, 160f, paint)

            canvas.drawText("Lagna (Ascendant): Mesha (मेष) | Moon Sign: Vrishabh", 50f, 195f, paint)
            canvas.drawText("Nakshatra: Ashwini Pada 3", 50f, 220f, paint)

            canvas.drawText("Planetary Positions & Dignity:", 50f, 260f, titlePaint.apply { textSize = 15f })
            val planets = listOf(
                "Sun (सूर्य) - Mesha (05°24') - Direct (Moolatrikona)",
                "Moon (चन्द्र) - Vrishabh (12°40') - Direct (Exalted)",
                "Mars (मंगल) - Simha (18°12') - Direct (Friendly)",
                "Mercury (बुध) - Mesha (22°15') - Direct (Combust)",
                "Jupiter (गुरु) - Vrishabh (08°30') - Direct (Friendly)",
                "Venus (शुक्र) - Meen (14°50') - Exalted",
                "Saturn (शनि) - Kumbh (25°10') - Retrograde",
                "Rahu (राहु) - Kumbh (10°05') - Retrograde",
                "Ketu (केतु) - Simha (10°05') - Retrograde"
            )
            var y = 295f
            for (p in planets) {
                canvas.drawText("• $p", 70f, y, paint)
                y += 26f
            }

            pdfDoc.finishPage(page)
            val file = File(context.cacheDir, "Kundli_${System.currentTimeMillis()}.pdf")
            val fos = FileOutputStream(file)
            pdfDoc.writeTo(fos)
            pdfDoc.close()
            fos.close()

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Kundli PDF Report"))
            Toast.makeText(context, "PDF तैयार और साझा करने के लिए तैयार है!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "PDF Export error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("जन्म कुंडली & वर्ग चार्ट (Kundli)") },
                navigationIcon = {
                    IconButton(onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        exportPdf()
                    }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", tint = SaffronPrimary)
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
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                            selectedTab = index
                        },
                        text = { Text(title, fontSize = 13.sp) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Brightness7, contentDescription = "Chart", tint = SaffronPrimary, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (selectedTab == 0) "उत्तर भारतीय लग्न कुंडली (North Indian Chart)" else "नवमांश D9 चार्ट (Navamsha Chart)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Pinch to zoom & pan chart | प्रोफाइल: ${activeProfile?.name ?: "साधक"}",
                                fontSize = 11.sp,
                                color = SaffronPrimary
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // Canvas North Indian Vedic Chart Render with Pinch-to-Zoom & Pan & Haptic
                            var scale by remember { mutableFloatStateOf(1f) }
                            var offset by remember { mutableStateOf(Offset.Zero) }
                            val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
                                scale = (scale * zoomChange).coerceIn(0.8f, 3.5f)
                                offset += offsetChange
                                if (zoomChange != 1f) {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                }
                            }

                            val primaryColor = SaffronPrimary
                            val onSurfaceColor = MaterialTheme.colorScheme.onSurface

                            Box(
                                modifier = Modifier
                                    .size(310.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .transformable(state = transformState),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(290.dp)
                                        .graphicsLayer(
                                            scaleX = scale,
                                            scaleY = scale,
                                            translationX = offset.x,
                                            translationY = offset.y
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.size(290.dp)) {
                                        val w = size.width
                                        val h = size.height

                                        // Outer Box
                                        drawRect(color = primaryColor, topLeft = Offset(0f, 0f), size = size, style = Stroke(width = 3.dp.toPx()))

                                        // Diagonals connecting corners
                                        drawLine(color = primaryColor, start = Offset(0f, 0f), end = Offset(w, h), strokeWidth = 2.dp.toPx())
                                        drawLine(color = primaryColor, start = Offset(w, 0f), end = Offset(0f, h), strokeWidth = 2.dp.toPx())

                                        // Diamond inner lines connecting midpoints
                                        val midX = w / 2f
                                        val midY = h / 2f
                                        drawLine(color = primaryColor, start = Offset(midX, 0f), end = Offset(w, midY), strokeWidth = 2.dp.toPx())
                                        drawLine(color = primaryColor, start = Offset(w, midY), end = Offset(midX, h), strokeWidth = 2.dp.toPx())
                                        drawLine(color = primaryColor, start = Offset(midX, h), end = Offset(0f, midY), strokeWidth = 2.dp.toPx())
                                        drawLine(color = primaryColor, start = Offset(0f, midY), end = Offset(midX, 0f), strokeWidth = 2.dp.toPx())
                                    }

                                    // Overlay House Text Labels & Planet placements
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(" 12 \n[केतु]", fontSize = 10.sp, color = onSurfaceColor)
                                            Text("  1  \n[सूर्य, बुध]", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                            Text(" 2 \n[शुक्र]", fontSize = 10.sp, color = onSurfaceColor)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(" 11 ", fontSize = 10.sp, color = onSurfaceColor)
                                            Text("   लग्न (Asc)\n   [चन्द्र, गुरु]", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                            Text(" 3 ", fontSize = 10.sp, color = onSurfaceColor)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(" 10 \n[शनि]", fontSize = 10.sp, color = onSurfaceColor)
                                            Text("  7  \n[मंगल]", fontSize = 10.sp, color = onSurfaceColor)
                                            Text(" 4 \n[राहु]", fontSize = 10.sp, color = onSurfaceColor)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text("Ascendant (Lagna): Mesha (मेष) | Lord: Mars", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                            Text("Nakshatra: Ashwini Pada 3", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { exportPdf() },
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("शेयर करने योग्य PDF रिपोर्ट निर्यात करें (Export PDF)", fontSize = 12.sp)
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("ग्रह स्थिति (Planetary Positions & Dignity)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(12.dp))

                            val planetList = listOf(
                                Triple("Sun (सूर्य)", "Mesha (05°24')", "Direct (Moolatrikona)"),
                                Triple("Moon (चन्द्र)", "Vrishabh (12°40')", "Direct (Exalted)"),
                                Triple("Mars (मंगल)", "Simha (18°12')", "Direct (Friendly)"),
                                Triple("Mercury (बुध)", "Mesha (22°15')", "Direct (Combust)"),
                                Triple("Jupiter (गुरु)", "Vrishabh (08°30')", "Direct (Friendly)"),
                                Triple("Venus (शुक्र)", "Meen (14°50')", "Exalted"),
                                Triple("Saturn (शनि)", "Kumbh (25°10')", "Retrograde (Swagrihi)"),
                                Triple("Rahu (राहु)", "Kumbh (10°05')", "Retrograde"),
                                Triple("Ketu (केतु)", "Simha (10°05')", "Retrograde")
                            )

                            planetList.forEach { (planet, position, status) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(planet, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                    Text(position, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(status, fontSize = 11.sp, color = SaffronPrimary, fontWeight = FontWeight.Bold)
                                }
                                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}
