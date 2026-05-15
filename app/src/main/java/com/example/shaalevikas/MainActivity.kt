package com.example.shaalevikas

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// --- Theme Colors ---
val Emerald600 = Color(0xFF059669)
val Emerald50 = Color(0xFFECFDF5)
val Amber500 = Color(0xFFF59E0B)
val Amber50 = Color(0xFFFFFBEB)
val Slate50 = Color(0xFFF8FAFC)
val Slate100 = Color(0xFFF1F5F9)
val Slate200 = Color(0xFFE2E8F0)
val Slate400 = Color(0xFF94A3B8)
val Slate500 = Color(0xFF64748B)
val Slate800 = Color(0xFF1E293B)
val Slate900 = Color(0xFF0F172A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainContainer()
            }
        }
    }
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Emerald600,
            secondary = Amber500,
            surface = Color.White,
            background = Slate50,
            onSurface = Slate800
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer() {
    var userLoggedIn by remember { mutableStateOf(false) }
    var currentView by remember { mutableStateOf("alumni") }

    val needs = remember { mutableStateListOf(
        Need(id="1", title = "Roof Repair (Block A)", description = "Monsoon leak fix.", costEstimate = 45000.0, collectedAmount = 32000.0, status = "open"),
        Need(id="2", title = "Completed Painting", description = "Fresh coat for the primary building.", costEstimate = 12000.0, collectedAmount = 12000.0, status = "completed")
           // imageUrlBefore = "https://picsum.photos/seed/old/400/300",
          //  imageUrlAfter = "https://picsum.photos/seed/new/400/300")
    ) }

    val donors = remember { mutableStateListOf(
        Donor(id="1", name = "Vikram Singh Rao", totalPledges = 12000.0, alumniYear = "94"),
        Donor(id="2", name = "Dr. Sunita Kini", totalPledges = 8500.0, alumniYear = "02")
    ) }

    if (!userLoggedIn) {
        LoginScreen { userLoggedIn = true }
    } else {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(currentView) { currentView = it }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (currentView) {
                    "alumni" -> AlumniHomeScreen(needs, donors) { needId ->
                        val index = needs.indexOfFirst { it.id == needId }
                        if (index != -1) {
                            val need = needs[index]
                            needs[index] = need.copy(collectedAmount = need.collectedAmount + 1000.0)
                        }
                    }
                    "headmaster" -> HeadmasterConsole(needs)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLogin: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Slate50).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo
        Box(
            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(24.dp)).background(Emerald600),
            contentAlignment = Alignment.Center
        ) {
            Text("SV", color = Color.White, fontWeight = FontWeight.Black, fontSize = 36.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Shaale-Vikas", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Slate900)
        Text("Empowering Rural Schools", fontSize = 16.sp, color = Slate500)

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
        ) {
            Text("Login as Alumni", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, Slate200)
        ) {
            Text("Continue with Google", fontWeight = FontWeight.Bold, color = Slate800)
        }
    }
}

@Composable
fun AlumniHomeScreen(needs: List<Need>, donors: List<Donor>, onPledge: (String) -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BentoHeader()

        // 1. ACTIVE NEEDS
        BentoCard(title = "Priority Micro-Needs", indicatorColor = Emerald600) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                needs.filter { it.status != "completed" }.forEach { need ->
                    NeedItem(need) {
                        onPledge(need.id)
                        Toast.makeText(context, "Pledge Recorded!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // 2. IMPACT GALLERY (SUCCESS STORIES)
        BentoCard(title = "Impact Gallery", subtitle = "Success Stories", indicatorColor = Amber500) {
            val completed = needs.filter { it.status == "completed" }
            if (completed.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    completed.forEach { need ->
                        ImpactPhotos(need.imageUrlBefore, need.imageUrlAfter, need.title)
                    }
                }
            } else {
                Text("Success stories coming soon...", color = Slate400, fontSize = 12.sp)
            }
        }

        // 3. DONORS
        BentoCardDark(title = "Alumni Hall of Fame") {
            donors.forEach { DonorItem(it) }
        }

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Amber500),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Make a General Pledge", fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun ImpactPhotos(before: String?, after: String?, title: String) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        Row(modifier = Modifier.fillMaxWidth().height(120.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(Slate200)) {
                AsyncImage(model = before, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Text("BEFORE", modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).background(Color.Black.copy(0.6f)).padding(horizontal = 4.dp), color = Color.White, fontSize = 9.sp)
            }
            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(Emerald50)) {
                AsyncImage(model = after, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Text("IMPACT", modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).background(Emerald600).padding(horizontal = 4.dp), color = Color.White, fontSize = 9.sp)
            }
        }
    }
}

@Composable
fun NeedItem(need: Need, onPledge: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Slate50).border(1.dp, Slate200, RoundedCornerShape(20.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(need.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Goal: ₹${need.costEstimate.toInt()}", color = Emerald600, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            LinearProgressIndicator(
                progress = { (need.collectedAmount / need.costEstimate).toFloat() },
                modifier = Modifier.padding(top = 12.dp).fillMaxWidth().height(6.dp).clip(CircleShape),
                color = Emerald600,
                trackColor = Slate200
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = onPledge, shape = RoundedCornerShape(12.dp)) {
            Text("Pledge", fontSize = 12.sp)
        }
    }
}

@Composable
fun BentoHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.White).border(1.dp, Slate200, RoundedCornerShape(24.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Emerald600), contentAlignment = Alignment.Center) {
            Text("SV", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
            Text("Shaale-Vikas", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Text("Govt. Rural School", color = Slate400, fontSize = 12.sp)
        }
    }
}

@Composable
fun BentoCard(title: String, subtitle: String? = null, indicatorColor: Color? = null, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(32.dp)).background(Color.White).border(1.dp, Slate200, RoundedCornerShape(32.dp)).padding(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (indicatorColor != null) Box(modifier = Modifier.size(4.dp, 20.dp).clip(CircleShape).background(indicatorColor))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
            if (subtitle != null) Text(subtitle, color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        content()
    }
}

@Composable
fun BentoCardDark(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(32.dp)).background(Slate900).padding(20.dp)) {
        Text(title, color = Amber500, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 16.dp))
        content()
    }
}

@Composable
fun DonorItem(donor: Donor) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Emerald600))
        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
            Text(donor.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("Batch of '${donor.alumniYear}", color = Color.White.copy(0.5f), fontSize = 11.sp)
        }
        Icon(Icons.Default.Star, null, tint = Amber500, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun BottomNavigationBar(currentView: String, onViewChange: (String) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(selected = currentView == "alumni", onClick = { onViewChange("alumni") }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
        NavigationBarItem(selected = currentView == "headmaster", onClick = { onViewChange("headmaster") }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Admin") })
    }
}

@Composable
fun HeadmasterConsole(needs: List<Need>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Headmaster Console", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) {
            Icon(Icons.Default.Add, null)
            Text("List a New Repair Need", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(needs) { NeedItem(it) { } }
        }
    }
}